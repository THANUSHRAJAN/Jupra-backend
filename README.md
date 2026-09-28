# JUPRA Backend (Spring Boot)

Stores every Contact-form and chatbot enquiry from the JUPRA website into your Supabase
PostgreSQL database, and powers the hidden founder dashboard at `/founderofjupra` on the
website (list enquiries, view full details, open the attached document, sign in with
email + password, and a "forgot password" flow using an emailed OTP).

> **Note on this build:** this project was written by hand to standard, well-documented
> Spring Boot 3.2 / jjwt 0.11.5 patterns, but it could **not be compiled inside the sandbox
> this was built in** (no access to Maven Central there). Please run `mvn clean package`
> locally, or just push it — Render's own build will compile it. If anything fails to
> compile, send me the error and I'll fix it immediately.

---

## 1. What it does

| Endpoint | Method | Auth | Purpose |
|---|---|---|---|
| `/api/contact` | POST (multipart) | Public | Save a Contact-form / chatbot enquiry |
| `/api/auth/login` | POST | Public | Founder sign-in → returns a JWT |
| `/api/auth/forgot-password` | POST | Public | Emails a 6-digit OTP |
| `/api/auth/verify-otp` | POST | Public | Checks the OTP → returns a short-lived reset token |
| `/api/auth/reset-password` | POST | Public | Sets a new password using the reset token |
| `/api/admin/me` | GET | **JWT required** | Confirms a stored session is still valid |
| `/api/admin/enquiries` | GET | **JWT required** | List all enquiries (table view) |
| `/api/admin/enquiries/{id}` | GET | **JWT required** | Full detail for one enquiry |
| `/api/admin/enquiries/{id}/document` | GET | **JWT required** | Streams the attached file inline |

Every `/api/admin/**` route requires a valid JWT, either as `Authorization: Bearer <token>`
or as `?token=<token>` (used by the "view document" link so it can open in a new tab).

## 2. Set up Supabase

1. In your Supabase project: **Settings → Database → Connection string → JDBC**.
2. Copy it — it looks like:
   `jdbc:postgresql://db.xxxxxxxxxxxx.supabase.co:5432/postgres?sslmode=require`
3. Note your database password (the one you set when the project was created, under
   **Settings → Database → Reset database password** if you don't have it).
4. You don't need to create any tables — this app creates them automatically on first
   startup (`enquiries`, `admin_users`, `password_reset_otps`).

## 3. Set up an email sender (for the OTP)

Any standard SMTP account works. Two easy options:

- **Gmail:** turn on 2-Step Verification, then create an **App Password**
  (Google Account → Security → App passwords). Use:
  `MAIL_HOST=smtp.gmail.com`, `MAIL_PORT=587`, `MAIL_USERNAME=<your gmail>`,
  `MAIL_PASSWORD=<the 16-character app password>`.
- **A transactional email service** (Brevo, SendGrid, etc. — all have a free tier): use the
  SMTP credentials they give you.

## 4. Environment variables

Set these in Render (**Environment** tab of your Web Service). Nothing is hardcoded in the code.

| Variable | Required | Example / notes |
|---|---|---|
| `SPRING_DATASOURCE_URL` | Yes | `jdbc:postgresql://db.xxxx.supabase.co:5432/postgres?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | Yes | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Yes | your Supabase DB password |
| `JWT_SECRET` | Yes | any long random string (32+ chars recommended) |
| `ADMIN_EMAIL` | Yes | the founder's login email, e.g. `jpra4425@gmail.com` |
| `ADMIN_PASSWORD` | No | leave blank to auto-generate one (see step 6) |
| `MAIL_HOST` | Yes | e.g. `smtp.gmail.com` |
| `MAIL_PORT` | Yes | e.g. `587` |
| `MAIL_USERNAME` | Yes | your SMTP username |
| `MAIL_PASSWORD` | Yes | your SMTP password / app password |
| `MAIL_FROM` | No | defaults to `MAIL_USERNAME` |
| `CORS_ALLOWED_ORIGINS` | Yes | comma-separated, e.g. `https://your-site.vercel.app,http://localhost:5173` |

`PORT` is set automatically by Render — you don't need to add it.

## 5. Deploy to Render

1. Push this folder to a GitHub repo (or upload it directly if you use Render's "Upload" flow).
2. In Render: **New → Web Service** → connect the repo.
3. Environment: **Docker** (Render will detect the `Dockerfile` automatically). Plan: Free is fine to start.
4. Add all the environment variables from the table above.
5. Deploy. Watch the logs — on first boot you'll see something like:
   ```
   =========================================================
    JUPRA founder account created
    Email:    jpra4425@gmail.com
    Password: aB3xQ9mK7pLr
    Sign in, then use 'Forgot password' to set your own password.
   =========================================================
   ```
   **Copy that password from the logs immediately** — it's only ever shown once. Sign in
   with it at `https://your-site.vercel.app/founderofjupra`, then use "Forgot password" to
   set a password you'll remember.
6. Copy your Render service URL (e.g. `https://jupra-backend.onrender.com`) — you'll need
   it for the frontend's `VITE_API_BASE_URL` (see the frontend's own README).

> Render's free plan spins the service down after inactivity — the first request after a
> quiet period can take 30–60 seconds to wake up. This is normal.

## 6. Run it locally (optional)

Requires Java 17 and Maven, and network access to Maven Central.

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://db.xxxx.supabase.co:5432/postgres?sslmode=require
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=your-password
export JWT_SECRET=some-long-random-string
export ADMIN_EMAIL=you@example.com
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=you@gmail.com
export MAIL_PASSWORD=your-app-password
export CORS_ALLOWED_ORIGINS=http://localhost:5173

mvn spring-boot:run
```

## 7. Notes / next steps

- File uploads (up to 10 MB, matching the frontend) are stored as bytes directly in the
  `enquiries` table — nothing extra to configure.
- Passwords and OTPs are hashed with BCrypt; OTPs expire after 10 minutes and can only be
  used once.
- The founder's session token is valid for 12 hours by default (`JWT_ACCESS_MINUTES`).
- This project intentionally skips a full security framework and a "register new admin"
  screen — it's built for a single founder account, reset only via the OTP email flow.
  If you'd like a second admin later, you can insert a row into `admin_users` directly
  (email + a BCrypt password hash), or ask me to add an "invite admin" endpoint.

## Importing into Spring Tool Suite (STS) / Eclipse

1. Unzip `jupra-backend.zip`. You should see a folder `jupra-backend` that directly contains
   `pom.xml`, `src/`, `Dockerfile` etc.
2. In STS: **File → Import → Maven → Existing Maven Projects → Next**.
3. **Root Directory → Browse** and select the `jupra-backend` folder (the one with `pom.xml`
   inside it — not its parent). The project should appear ticked → **Finish**.
   (Alternatively: *General → Existing Projects into Workspace* also works, because
   `.project` / `.classpath` are included.)
4. Wait for Maven to download dependencies (bottom-right progress). Needs internet access.
5. To run: right-click `BackendApplication.java` → **Run As → Spring Boot App**, after setting
   the environment variables from section 4 under **Run Configurations → Environment**.
6. Use **Java 17** (Window → Preferences → Java → Installed JREs). If Maven shows a
   "release version 17 not supported" error, point STS to a JDK 17 or newer.
