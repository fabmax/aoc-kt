# Advent of code 2024 (in kotlin)

My solutions to the puzzles of [Advent of code 2024](https://adventofcode.com/2023/)

## Results

- **[Day 1:](src/main/kotlin/y2024/day01/Day01.kt) Historian Hysteria**
  Nice warm-up!

 
  
## Running the Puzzles

In order to run the puzzles, you need to place your puzzle input into correctly named `.txt` files in the `inputs/2024/` directory:
The implementation expects the day's puzzle input in a file called `day[xx].txt` where `[xx]` has to be replaced by
the day number (e.g. `day01.txt` for day 1, `day10.txt` for day 10, etc.)

Moreover, test-input can be specified in separate `.txt` files in the same directory: `day01_test.txt` for day 1's
test input and so on. Multiple test inputs for the same day can be given by appending an extra number:
`day02_test1.txt`, `day02_test2.txt`, etc.

Test input files are expected to start with a single line containing the expected results (if already known):
```
test1=?; test2=?; part1=?; part2=?

[test input here]
```
Where the `?` can be replaced with the expected results for part 1 / part 2 (for test input and main puzzle). You can
also keep the `?` or remove the entire entry if the expected result is not yet known. Moreover, if the expected
test result is only specified for a single part, only that part is executed.

## Previous Advents of Code

From time to time I solve puzzles from the previous years. Solutions are located in their individual packages:

- [y2023](src/main/kotlin/y2023): All solutions for year 2023 (50 stars)
- [y2022](src/main/kotlin/y2022): All solutions for year 2022 (50 stars)
- [y2015](src/main/kotlin/y2015): Days 1 to 21 for year 2015 (42 stars so far)
