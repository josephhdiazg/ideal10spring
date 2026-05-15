# Contributing Guide

This document defines the Git workflow and contribution rules for the IDEAL Predial 10 project.

## Branching Strategy

The repository uses two main branches:

- `main`: stable branch. Only reviewed and approved changes should be merged here.
- `develop`: integration branch. All feature branches must be merged here before reaching `main`.

## Team Feature Branches

Each team member must work on their assigned module branch:

| Team member | Responsibility | Branch |
| --- | --- | --- |
| Joseph | Authentication, authorization, users, roles, and security | `feature/security-module` |
| Omar | Municipalities, properties, owners, and property ownership | `feature/cadastre-module` |
| Michael | Fiscal years, tax rates, benefits, and charge concepts | `feature/tax-configuration-module` |
| Juan | Assessments, payments, clearance certificates, and dashboard | `feature/assessment-payments-module` |
| Team | Frontend integration and role-based user interface | `feature/frontend-module` |

Each module branch should be merged into:

```bash
develop
```

## Commit Messages

All commit messages must be written in English.

Use Conventional Commits with short, clear, and descriptive messages:

```text
<type>: <description>
```

Recommended types:

- `feat`: new feature or functional capability.
- `fix`: bug fix.
- `docs`: documentation-only change.
- `config`: configuration change.
- `test`: test change.
- `refactor`: code change that does not add behavior or fix a bug.
- `chore`: maintenance change.

Good examples:

```bash
feat: add municipality entity
feat: create property owner repository
config: configure PostgreSQL datasource
fix: validate cadastral code uniqueness
feat: add property ownership endpoints
```

Bad examples:

```bash
cosas
arreglos
subiendo cambios
fix
avance omar
```

## Recommended Commit Format

Use this format when possible:

```text
<type>: <what changed>
```

Examples:

```bash
feat: add owner request DTO
fix: update property validation rules
fix: fix municipality lookup by id
refactor: remove unused cadastral service method
```

## Pull Request Rules

Before opening a pull request:

1. Make sure the project compiles.
2. Make sure the branch is updated with `develop`.
3. Verify that the change belongs to your assigned module.
4. Do not include unrelated files or IDE-only changes.
5. Write the pull request title and description in English.

Pull requests should target `develop`, not `main`.

## General Rules

- Do not commit database passwords or personal credentials.
- Do not commit generated build folders such as `target/`.
- Keep changes small and focused.
- Use meaningful class, method, package, and branch names.
- Coordinate with the team before changing shared entities or package structure.
