# ideal10spring

## Test users

The application seeds one user per role on startup:

| Username | Password | Role |
| --- | --- | --- |
| `admin@ideal10.com` | `admin123` | `ROLE_ADMINISTRADOR` |
| `hacienda@ideal10.com` | `hacienda123` | `ROLE_FUNCIONARIO_HACIENDA` |
| `tesoreria@ideal10.com` | `tesoreria123` | `ROLE_TESORERIA` |
| `contribuyente@ideal10.com` | `contribuyente123` | `ROLE_CONTRIBUYENTE` |

Login endpoint:

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin@ideal10.com",
  "password": "admin123"
}
```
