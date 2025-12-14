# Sokoban Game

## Overview
This is a JavaFX-based game project. The project uses Java 21 and JavaFX modules

---

## Requirements
- **Java 21** installed
- **IntelliJ IDEA** 
- **JavaFX SDK** (included in this project under `lib/` folder)

---

## Running the Game

### Option 1: Run using IntelliJ IDEA
1. Open the project in IntelliJ IDEA.
2. Download the JavaFX SDK from [Gluon](https://openjfx.io/).
3. Extract the zipped JavaFX folder
4. In IntelliJ IDEA, go to `File → Project Structure → Libraries → + → Java` and select the `lib/` folder from your downloaded JavaFX SDK.
5. Set the VM options in the Run Configuration for the main class:
`--module-path C:\javafx\lib --add-modules javafx.controls,javafx.graphics`
6. Run the main class