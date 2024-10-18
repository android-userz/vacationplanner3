
Vacation Planner App

Description
The Vacation Planner App is an Android application that helps users organize and plan their vacations by creating and managing both vacations and excursions. Users can add vacations, view and edit details, plan excursions within vacations, and set notifications for upcoming excursions.
 Features
- Add, edit, and delete vacations.
- Add, edit, and delete excursions within vacations.
- Date validation ensures excursions fall within the vacation dates.
- Notifications for upcoming excursions.
- Simple, user-friendly interface.
 Screenshots
Application Flow
1. Vacation List Screen: Displays a list of planned vacations.
2. Vacation Detail Screen: Shows details of a selected vacation.
3. Excursion List Screen: Lists excursions under a selected vacation.
4. Excursion Detail Screen: Allows viewing and editing of excursion details.
5. Add/Edit Screens: Forms to add or edit vacations and excursions.
6. Delete Functionality: Delete vacations or excursions with confirmation dialogs.

 Installation and Setup
 Prerequisites
- Android Studio
- Android SDK
 Steps to Install
1. Clone the repository:
    
    https://github.com/android-userz/vacationplanner2
2. Open the project in Android Studio.
3. Build the project:
    - Click on `Build` > `Make Project` or press `Ctrl+F9`.
4. Run the app on an emulator or physical device:
    - Click on `Run` > `Run 'app'` or press `Shift+F10`.

 Dependencies
- Room Database for local storage.
- LiveData and ViewModel for handling UI-related data in a lifecycle-conscious way.
- Material Design for UI components.
- NotificationManager for scheduling and canceling notifications.

 How to Use
1. Add a Vacation:
    - Click on the "+" button in the vacation list to add a new vacation.
    - Enter the vacation title and dates.
    - Save the vacation to see it in the list.
  
2. Manage Excursions:
    - Select a vacation to view its details.
    - Click on the "View Excursions" button to see excursions planned for that vacation.
    - You can add, edit, or delete excursions using the buttons available.

3. Delete Vacation or Excursion:
    - Open the vacation or excursion you wish to delete.
    - Click the "Delete" button, and confirm the deletion in the dialog that appears.

 Application Structure
- Activities:
  - `MainActivity`: The main entry point that shows the vacation list.
  - `ExcursionListActivity`: Displays a list of excursions for a selected vacation.
  - `ExcursionDetailActivity`: For viewing and editing excursion details.

- Database:
  - Uses Room for local database storage.
  - `AppDatabase`: Main database class.
  - `VacationDao` and `ExcursionDao`: Data access objects for performing database operations.

- Adapters:
  - `ExcursionAdapter`: For managing the RecyclerView of excursions.

- Utils:
  - `NotificationHelper`: Utility class to handle notifications for excursions.


 Contribution Guidelines
1. Fork the repository.
2. Create a new branch (`feature/your-feature` or `fix/your-bugfix`).
3. Commit your changes.
4. Push to the branch.
5. Open a Pull Request for review.
