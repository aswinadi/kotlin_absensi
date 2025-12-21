---
description: How to add new features with git branching
---

# Git Workflow for Feature Development

## Branch Structure

```
main (production-ready)
  │
  └── develop (integration branch)
        │
        ├── feature/check-in-out
        ├── feature/notifications
        └── fix/login-error
```

## Starting a New Feature

### 1. Create GitHub Issue (Optional but Recommended)

Go to GitHub → Issues → New Issue
- Title: "Add Check-In/Check-Out Feature"
- Labels: `enhancement`, `priority:high`
- Milestone: "Week 2"

### 2. Create Feature Branch

```bash
# Make sure you're on develop and up to date
git checkout develop
git pull origin develop

# Create feature branch
git checkout -b feature/check-in-out
```

### 3. Make Commits (Conventional Commits)

// turbo
```bash
git add .
git commit -m "feat(checkin): add check-in screen UI"
```

**Commit Types:**
- `feat` - New feature
- `fix` - Bug fix  
- `docs` - Documentation
- `refactor` - Code restructure
- `style` - Formatting
- `test` - Tests
- `chore` - Build/tooling

### 4. Push Feature Branch

// turbo
```bash
git push -u origin feature/check-in-out
```

### 5. Create Pull Request

Go to GitHub → Pull Requests → New PR
- Base: `develop`
- Compare: `feature/check-in-out`
- Title: "feat: Add Check-In/Check-Out Feature"
- Body: "Closes #12" (if linked to issue)

### 6. After PR is Merged

// turbo
```bash
git checkout develop
git pull origin develop
git branch -d feature/check-in-out
```

## Releasing to Production

When develop is stable and ready for release:

```bash
git checkout main
git pull origin main
git merge develop
git push origin main
git tag v1.0.0
git push origin v1.0.0
```

## Quick Reference

| Action | Command |
|--------|---------|
| Start feature | `git checkout -b feature/name` |
| Push feature | `git push -u origin feature/name` |
| Update from develop | `git pull origin develop` |
| Merge to develop | Create PR on GitHub |
| Delete local branch | `git branch -d feature/name` |

## Commit Message Examples

```bash
feat(auth): add biometric login support
fix(home): resolve null pointer on empty schedule
docs(readme): update installation instructions
refactor(profile): extract leave quota component
style: format code with ktlint
test(login): add unit tests for validation
chore: update gradle dependencies
```
