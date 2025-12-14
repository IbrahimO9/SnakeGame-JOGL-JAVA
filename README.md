# Snake Game with JOGL (Java OpenGL)

This is a Snake Game implementation using the Java JOGL library.

## Requirements

- Java JDK 1.8.0_333
- JOGL Library
- Eclipse IDE (Optional, but instructions are for Eclipse)

## Installation Instructions

Follow these steps to set up the development environment using the provided `Setup.rar`.

### 1. Prerequisite Cleanup

- Delete/Uninstall any existing Eclipse IDE and Java SDK installations to avoid conflicts, as we will use specific versions provided.

### 2. JDK Installation & Configuration

1.  Extract `Setup.rar`.
2.  Install the JDK provided in the archive (JDK 1.8.0_333).
3.  **Environment Variables Setup**:
    - Navigate to the installation directory, commonly `C:\Program Files\Java\jdk1.8.0_333\bin`. Copy this path.
    - Open "Edit the system environment variables" on Windows.
    - **User Variables**:
      - Click `New`.
      - Variable Name: `JAVA_HOME`
      - Variable Value: Paste the path to the bin folder (e.g., `C:\Program Files\Java\jdk1.8.0_333\bin`).
    - **System Variables**:
      - Find the `Path` variable and click `Edit`.
      - Click `New` and paste the same bin path.
      - Click `OK` to save.

### 3. Eclipse & JOGL Setup

1.  Install the Eclipse IDE provided in the `Setup.rar`.
2.  Move the `JOGL20` folder (from the setup files) to the `C:\` drive (so path is `C:\JOGL20`).
3.  Open Eclipse.
4.  Go to `Window` -> `Preferences`.
5.  Navigate to `Java` -> `Build Path` -> `User Libraries`.
6.  Click `New`, type **JOGL**, and click `OK`.
7.  With the new "JOGL" library selected, click `Add External JARs`.
8.  Navigate to `C:\JOGL20\lib` and select the following 4 files:
    1.  `gluegen-rt.jar`
    2.  `jogl-all.jar`
    3.  `nativewindow-all.jar`
    4.  `newt-all.jar`
9.  **Native Library Location**:
    - Expand each of the 4 JAR listings in the User Library.
    - Select "Native library location", click `Edit`, and choose `External Folder`.
    - Select the `C:\JOGL20\lib` folder.
    - Repeat this for all 4 JARs.
10. Click `Apply and Close`.

### 4. Project Setup

1.  Create a new Java Project in Eclipse.
2.  Name the project (e.g., `SnakeGame`).
3.  **Important**: Before clicking Finish, look for "JRE" settings. Select "Use a project specific JRE" and ensure it is selected as `1.8.0_333`. (You may need to configure JREs if not listed).
4.  Click `Next`.
5.  Go to the `Libraries` tab.
6.  Click `Add Library` -> `User Library`.
7.  Select the **JOGL** library we created earlier.
8.  Click `Finish`.

### 5. Running the Code

1.  Create a new class in `src` (e.g., `Snake.java` or `Test.java`).
2.  Copy the game code into this class.
3.  Run the project.
4.  If successful, you should see the game window (or a red box test if using test code).

## Project Structure

- `src/test/Snake.java`: Main game code.
- `Sound/`: Contains game audio assets.
- `Setup.rar`: Contains the required JDK, Eclipse, and JOGL libraries for setup.
