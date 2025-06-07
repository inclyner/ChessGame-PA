# ChessGame-PA ♟️

A full-featured **JavaFX Chess Game**, developed as part of the *Advanced Programming* course at ISEC. This project demonstrates clean architecture, a modular structure, and the use of key **software design patterns**.

## 🚀 Features

- Graphical interface built with JavaFX (FXML + CSS)
- Fully functional chess logic with all rules
- Save/Load game state using **serialization** and the **Memento Pattern**
- Multiple game modes (Player vs Player, Player vs Bot)
- Move history with **Undo/Redo** support
- Menu navigation (Start screen, Game, Credits, etc.)
- Scalable code structure following **MVC architecture**

---

## 🧠 Design Patterns Used

| Pattern     | Purpose                                                                 |
|-------------|-------------------------------------------------------------------------|
| **MVC**     | Separates the logic (`model`), interface (`view`) and control flow (`controller`) |
| **Memento** | Saves and restores game state with serialization                        |
| **Factory** | Creates chess pieces and controllers dynamically                        |
| **Observer** (optional/implicit) | Manages UI updates on model changes in some cases       |

---

## 📁 Project Structure

```plaintext
├── model/       # Core game logic (board, pieces, rules)
├── view/        # JavaFX GUI (FXML, CSS, layout)
├── controller/  # Connects model to the view (input handling, flow control)
├── util/        # Utility classes (serialization, helpers, state manager)
├── resources/   # Images, FXML files, audio
