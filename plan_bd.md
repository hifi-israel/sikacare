# Plan de Base de Datos — SikaCare (Supabase + PostgreSQL)

## 1) Alcance inicial
- **Autenticación**: Login, registro y recuperación de contraseña (usando Supabase Auth, sin guardar contraseñas en tablas propias).
- **Onboarding ("novedades")**: Controlar si el usuario ya vio la pasarela inicial (next/next/Comenzar).
- **Perfil básico**: Nombre visible, teléfono (8 dígitos), género (M/F/O), fecha de nacimiento y avatar elegido de una lista predefinida.

Pantallas revisadas en el código:
- `composeApp/src/commonMain/kotlin/com/israeljuarez/sikacorekmp/login/LoginScreen.kt`: Email y contraseña.
- `composeApp/src/commonMain/kotlin/com/israeljuarez/sikacorekmp/login/RegisterScreen.kt`: Nombre, email, teléfono (8 dígitos), contraseña, confirmar y código de verificación.
- `composeApp/src/commonMain/kotlin/com/israeljuarez/sikacorekmp/login/ForgotPasswordScreen.kt`: Email, nueva contraseña, confirmar y código de verificación.

Nota: La app luego pedirá edad (recomendado guardar fecha de nacimiento) y género, más la selección de un ícono/avatar de una lista.

---

## 2) Decisiones clave de diseño
- **Usar Supabase Auth (`auth.users`)**: No creamos tabla de usuarios para email/contraseña. `auth.users` provee `id (uuid)`, `email`, `email_confirmed_at`, etc. Esto asegura unicidad de correo, verificación y seguridad.
- **Perfil 1:1 con `auth.users`**: Creamos `public.profiles` con PK = `user_id (uuid)` referenciando `auth.users(id)`.
- **Onboarding dentro de `profiles`**: Un booleano `is_onboarding_seen` cumple el rol de "novedades". Evitamos una tabla aparte salvo que más adelante necesitemos historial/fechas.
- **Avatares predefinidos**: Catálogo `public.avatars` con íconos por defecto. `profiles.avatar_id` referencia a `avatars(id)`.
- **Edad**: Guardar `birthdate (date)` y calcular edad cuando se necesite. Es preferible a guardar un entero mutable.
- **Género**: Tipo enumerado `gender_type` con valores `M`, `F`, `O` (otros/no binario). Se puede expandir en el futuro si se requieren más opciones.
- **RLS (Row Level Security)**: Activada. Cada usuario solo puede ver/editar su propio perfil. `avatars` es de lectura pública.
- **Triggers**: `handle_new_user` para crear automáticamente el `profile` al registrarse; `set_updated_at` para mantener `updated_at`.

---

## 3) Diagrama (alto nivel)
```mermaid
erDiagram
  AUTH_USERS ||--|| PROFILES : "user_id = id"
  PROFILES }o--|| AVATARS : "avatar_id -> id"

  AUTH_USERS {
    uuid id PK
    text email
    timestamptz email_confirmed_at
  }
  PROFILES {
    uuid user_id PK, FK
    text full_name
    text phone
    boolean is_onboarding_seen
    gender_type gender
    date birthdate
    int4 avatar_id FK
    timestamptz created_at
    timestamptz updated_at
  }
  AVATARS {
    serial id PK
    text key UNIQUE
    text name
    text image_url
    boolean active
    timestamptz created_at
    timestamptz updated_at
  }
```

---

## 4) Esquema SQL propuesto (para ejecutar en Supabase SQL Editor)

```sql
-- Asegurar dependencias comunes
create extension if not exists pgcrypto with schema public;

-- 4.1) Tipo enumerado para género
do $$
begin
  if not exists (
    select 1
    from pg_type t
    join pg_namespace n on n.oid = t.typnamespace
    where t.typname = 'gender_type' and n.nspname = 'public'
  ) then
    create type public.gender_type as enum ('M', 'F', 'O');
  end if;
end
$$;

-- 4.2) Tabla de avatares (catálogo)
create table if not exists public.avatars (
  id            serial primary key,
  key           text not null unique,
  name          text not null,
  image_url     text not null,
  active        boolean not null default true,
  created_at    timestamptz not null default now(),
  updated_at    timestamptz not null default now()
);

-- 4.3) Tabla de perfiles 1:1 con auth.users
create table if not exists public.profiles (
  user_id            uuid primary key references auth.users(id) on delete cascade,
  full_name          text,
  phone              text check (phone ~ '^[0-9]{8}$'),
  is_onboarding_seen boolean not null default false,
  gender             public.gender_type,
  birthdate          date,
  avatar_id          int references public.avatars(id),
  created_at         timestamptz not null default now(),
  updated_at         timestamptz not null default now()
);

-- 4.4) Índices
create index if not exists idx_profiles_avatar_id on public.profiles(avatar_id);
create index if not exists idx_profiles_created_at on public.profiles(created_at);
create index if not exists idx_avatars_active on public.avatars(active);

-- 4.5) Trigger genérico para updated_at
create or replace function public.set_updated_at()
returns trigger language plpgsql as $$
begin
  new.updated_at := now();
  return new;
end;
$$;

-- Asociar trigger a tablas con updated_at
drop trigger if exists trg_profiles_set_updated_at on public.profiles;
create trigger trg_profiles_set_updated_at
before update on public.profiles
for each row execute function public.set_updated_at();

drop trigger if exists trg_avatars_set_updated_at on public.avatars;
create trigger trg_avatars_set_updated_at
before update on public.avatars
for each row execute function public.set_updated_at();

-- 4.6) Auto-creación de profile al registrarse un usuario
create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = public as $$
begin
  insert into public.profiles(user_id, full_name)
  values (new.id, coalesce(new.raw_user_meta_data->>'full_name', ''))
  on conflict (user_id) do nothing;
  return new;
end;
$$;

-- Asegurar el trigger en auth.users (usar EXECUTE FUNCTION)
drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
  after insert on auth.users
  for each row execute function public.handle_new_user();

-- 4.7) Habilitar RLS
alter table public.profiles enable row level security;
alter table public.avatars enable row level security;

-- Policies para profiles (con guardas para rerun)
do $$
begin
  if not exists (
    select 1 from pg_policies
    where schemaname = 'public' and tablename = 'profiles' and policyname = 'profiles_select_own'
  ) then
    create policy profiles_select_own on public.profiles
      for select using (auth.uid() = user_id);
  end if;

  if not exists (
    select 1 from pg_policies
    where schemaname = 'public' and tablename = 'profiles' and policyname = 'profiles_insert_own'
  ) then
    create policy profiles_insert_own on public.profiles
      for insert with check (auth.uid() = user_id);
  end if;

  if not exists (
    select 1 from pg_policies
    where schemaname = 'public' and tablename = 'profiles' and policyname = 'profiles_update_own'
  ) then
    create policy profiles_update_own on public.profiles
      for update using (auth.uid() = user_id) with check (auth.uid() = user_id);
  end if;
end
$$;

-- Policy para avatars: lectura pública (con guarda)
do $$
begin
  if not exists (
    select 1 from pg_policies
    where schemaname = 'public' and tablename = 'avatars' and policyname = 'avatars_read_all'
  ) then
    create policy avatars_read_all on public.avatars
      for select using (true);
  end if;
end
$$;


---

## 5) Semilla (seed) de avatares sugerida

```sql
insert into public.avatars (key, name, image_url) values
  ('avatar_1', 'Avatar Azul', 'https://<your-project-ref>.supabase.co/storage/v1/object/public/avatars/avatar_1.png'),
  ('avatar_2', 'Avatar Verde', 'https://<your-project-ref>.supabase.co/storage/v1/object/public/avatars/avatar_2.png'),
  ('avatar_3', 'Avatar Rojo', 'https://<your-project-ref>.supabase.co/storage/v1/object/public/avatars/avatar_3.png')
  on conflict (key) do nothing;
```

Para soportar estas URLs:

```sql
-- Crear bucket público para íconos/avatares
select storage.create_bucket('avatars', public => true);
-- Opcional: configurar una política de lectura pública a nivel de Storage si fuera necesario
```

Luego, sube los archivos `avatar_1.png`, `avatar_2.png`, `avatar_3.png` al bucket `avatars/` desde el Dashboard de Supabase.

---

## 6) Consultas y ejemplos (solo referencia)
- Obtener perfil del usuario autenticado:
```sql
select p.*
from public.profiles p
where p.user_id = auth.uid();
```

- Marcar onboarding como visto (al tocar "Comenzar"):
```sql
update public.profiles
set is_onboarding_seen = true
where user_id = auth.uid();
```

- Actualizar nombre, género, fecha de nacimiento y avatar:
```sql
update public.profiles
set full_name = 'Nuevo Nombre',
    gender = 'M',
    birthdate = '2000-05-12',
    avatar_id = 2
where user_id = auth.uid();
```

---

## 7) Alternativa si deseas separar "novedades"
Si prefieres una tabla específica para el estado de onboarding (por claridad o para auditar fechas), puedes crear:

```sql
create table if not exists public.user_onboarding (
  user_id     uuid primary key references auth.users(id) on delete cascade,
  seen        boolean not null default false,
  seen_at     timestamptz,
  created_at  timestamptz not null default now(),
  updated_at  timestamptz not null default now()
);

alter table public.user_onboarding enable row level security;
create policy if not exists user_onboarding_select_own on public.user_onboarding
  for select using (auth.uid() = user_id);
create policy if not exists user_onboarding_upsert_own on public.user_onboarding
  for insert with check (auth.uid() = user_id);
create policy if not exists user_onboarding_update_own on public.user_onboarding
  for update using (auth.uid() = user_id) with check (auth.uid() = user_id);

create trigger trg_user_onboarding_set_updated_at
before update on public.user_onboarding
for each row execute function public.set_updated_at();
```

En este plan inicial, mantenerlo dentro de `profiles.is_onboarding_seen` es suficiente y más simple.

---

## 8) Pasos para aplicar este plan
- **SQL**: Copia el bloque del punto 4 en el SQL Editor de Supabase y ejecútalo.
- **Storage**: Crea el bucket `avatars` y sube las imágenes. Luego ejecuta el bloque de seed del punto 5 (ajusta las URLs con tu `project-ref`).
- **Verificación**: Crea un usuario de prueba desde Auth, confirma su email; verifica que se cree el `profiles` asociado gracias al trigger `handle_new_user`.

---

## 9) Pendientes y futuras extensiones
- Validación de formato de teléfono por país/región si se amplía el alcance.
- Tabla de métricas de salud futuras (IMC, etc.) fuera del alcance actual.
- Auditoría de cambios (tabla de logs) si se requiere cumplimiento/regulación.
- Si en el futuro se permiten avatares personalizados, crear relación con Storage por `user_id` y políticas específicas.

---

## 10) Resumen
- 2 tablas principales: `profiles` (1:1 con `auth.users`) y `avatars` (catálogo).
- `is_onboarding_seen` cubre tu caso de "novedades" sin tabla separada.
- RLS estricta en `profiles`; lectura pública en `avatars`.
- Triggers para autocreación de perfil y mantenimiento de `updated_at`.
- Listo para login/registro/reset por Supabase Auth y perfil básico (nombre, género, fecha, avatar, teléfono).
