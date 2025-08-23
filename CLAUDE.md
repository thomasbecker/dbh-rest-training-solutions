# CLAUDE.md - DBH REST with Jersey Training

This file provides guidance to Claude Code when working with the DBH REST training materials.

## Project Overview

This repository contains training materials for a 2-day REST API workshop using Jersey (plain Java, no Spring Boot) for DBH. The training targets Java 8 and emphasizes hands-on exercises.

## Slide Creation with Presenterm

Presenterm is an alternative terminal presentation tool with additional features. Located at https://github.com/mfontanini/presenterm

### Presenterm Syntax

#### Slide Separators

- `---` to separate slides (standard)
- `<!-- end_slide -->` for explicit slide ending

#### Code Blocks with Features

````markdown
```java +line_numbers
@Path("/users")
public class UserResource {
    // code here
}
```
````

```java {1-3|5-8|all}  // Dynamic line highlighting
// First highlight lines 1-3
// Then highlight lines 5-8
// Finally highlight all
```

````

#### Speaker Notes
```markdown
## Slide Title

Content visible to audience

<!-- speaker_note: This is a single line speaker note -->

More content

<!--
speaker_note: |
  This is a multiline speaker note.
  Remember to mention:
  - Point 1
  - Point 2
  - Point 3
-->
````

#### Layouts and Columns

```markdown
<!-- column_layout: [7, 3] -->

<!-- column: 0 -->

## Main Content

Left column with 70% width

<!-- column: 1 -->

### Sidebar

Right column with 30% width

<!-- reset_layout -->
```

#### Pauses and Animations

```markdown
## Progressive Content

First point

<!-- pause -->

Second point appears after pause

<!-- pause -->

Third point
```

#### Executable Code

````markdown
```bash +exec
echo "This will execute when shown"
```
````

````

### Running Presenterm
```bash
# Install presenterm
cargo install presenterm

# Run presentation (basic)
presenterm slides/presenterm/presentation.md

# With speaker notes - Terminal 1 (presenter view)
presenterm slides/presenterm/presentation.md --publish-speaker-notes

# With speaker notes - Terminal 2 (listener for notes)
presenterm slides/presenterm/presentation.md --listen-speaker-notes

# Or configure to always publish in ~/.config/presenterm/config.yaml
# speaker_notes:
#   always_publish: true
````

## Development Workflow

1. Research using Context7, Brave Search, and Firecrawl
2. Create base project structure
3. Develop exercises progressively
4. Create slides in Presenterm format for each exercise
5. Test all code with Java 8
6. Validate exercise timing

## Exercise Structure Standards

Based on Exercise 02, maintain this structure for all exercises:

### Exercise Folder Contents

- `README.md` - Written instructions for students (self-study/reference)
- `INSTRUCTOR_NOTES.md` - Teaching notes and common issues
- Slides in `/slides/presenterm/` - Presenterm slides for live presentation

### README Structure (for self-study)

1. **Header** - Exercise number, title, time allocation, objectives
2. **Prerequisites** - What needs to be completed first
3. **Background** - Context and motivation
4. **Your Tasks** - Numbered tasks with:
   - Clear task titles and time estimates
   - Step-by-step instructions
   - Code hints and tips
   - Links to relevant documentation
5. **Running the Tests** - Commands and TDD workflow explanation
6. **Expected Test Output** - What success looks like
7. **Hints** - Technical tips (data structures, patterns, etc.)
8. **Bonus Tasks** - Challenges for fast finishers with time estimates
9. **Helpful Resources** - Documentation links and references
10. **Common Mistakes to Avoid** - Numbered list with ❌ emoji
11. **Solution Checkpoint** - Checklist of what should be complete
12. **Need Help?** - Escalation path for stuck students

### Slide Structure (Presenterm format)

1. **Title slide** - Exercise name, duration, goal
2. **Mission/Overview** - What students will build
3. **Task slides** - One slide per task/subtask with:
   - Task number and title
   - Documentation link (📚 emoji)
   - Hint before implementation (💡 emoji)
   - `<!-- pause -->` for progressive revelation
   - Code example after pause
   - Expected result/status code
4. **Reference slides**:
   - HTTP status codes table
   - Common mistakes
   - Testing instructions
5. **Checkpoint slides** - At 20 min, 40 min marks
6. **Bonus tasks slide** - List with brief descriptions
7. **Key takeaways** - 4-5 main points with pauses
8. **Questions/Next** - Transition to next exercise

### Slide Best Practices

- Keep slides screen-sized (use `<!-- end_slide -->` liberally)
- Start each task slide with docs + hint, then pause, then code
- Use progressive revelation (`<!-- pause -->`) for step-by-step
- Include speaker notes for instructor guidance
- Emoji usage: 📚 for docs, 💡 for hints, ✅ for success, ❌ for errors
- Time checkpoints help instructors pace the session

### Alignment Requirements

- Tasks in slides MUST match README tasks (same numbering, same content)
- Time allocations must be consistent
- Bonus tasks must be identical
- Test commands must match

## Key Commands

```bash
# Build base project
cd base-project
./gradlew build

# Run tests
./gradlew test

# Start server
./gradlew run

# Test with curl
curl -X GET http://localhost:8080/api/users
```

## Solution Management Strategy

### Directory Structure

- `/starter-project/` - Always contains skeleton code with TODOs for attendees to complete
- `/instructor-solution/` - Contains complete solutions for instructor reference

### Branch Strategy

**Main Branch:**

- Contains the FIRST complete solution for each file in `/instructor-solution/`
- Example: `UserResource.java` first appears in Exercise 02, so its Exercise 02 solution lives in `/instructor-solution/` on main
- Example: If `ValidationExceptionMapper.java` first appears in Exercise 03, its Exercise 03 solution also lives on main

**Solution Branches:**

- Only created when existing files need MODIFICATIONS in later exercises
- Branch naming: `solution/03-validation`, `solution/04-versioning`, etc.
- Example: `solution/03-validation` contains updated `UserResource.java` with validation added
- Example: `solution/04-versioning` contains updated `UserResource.java` with versioning added
- New files introduced in that exercise still go to main branch first

### Key Principles

1. Each file's initial solution goes in main branch
2. Branches only for exercises that modify existing files
3. Starter-project always has skeletons/TODOs
4. Instructor-solution accumulates all "first appearance" solutions on main
5. Branches only contain delta changes to existing files
6. Less duplication, clear progression

### Instructor Workflow

```bash
# Show initial solution (from main)
cat instructor-solution/src/main/java/com/dbh/training/rest/resources/UserResource.java

# Show how file evolves in Exercise 03
git checkout solution/03-validation
git diff main..HEAD -- instructor-solution/src/main/java/com/dbh/training/rest/resources/UserResource.java

# Return to main for new files
git checkout main
```

## Java 8 Setup Required

**IMPORTANT**: This project requires Java 8 to run. Use:

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home
# Or your Java 8 path
```

## Java 8 Compatibility Checklist

- [ ] No var keyword
- [ ] No modules (module-info.java)
- [ ] No text blocks
- [ ] No switch expressions
- [ ] Use Jersey 2.35 (not 3.x)
- [ ] Use Jackson 2.14.x
- [ ] Lambda expressions OK
- [ ] Stream API OK
- no co-authored-by stuff when doing commits
- remember that this repo will be split into multiple smaller repos later.
- Remember how to run gradle and the tests with the correct java version, etc.
- no auto commits. I want to commit manually
- no shell scripts to start the presentations

- Remember how to end slides
- no auto-commits! I want to commit manually. Thought I added that rule already to claude.md. Why are you ignoring that?