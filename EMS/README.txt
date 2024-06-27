DataBase name: emsDB
Username: administrator
Password: password

Additional Use Case:
Edit event details in Manage My Events page

Business Logic:
Profile page fulfils both view my profile and edit profile use cases. It displays the current user information. 

If user wants to change personal information, user can edit the information and the updated information will be displayed upon updating the profile.

User is assumed the role of organiser for the events they created. They would not be able to join as participants.

Event Hub shows all the events except the events that the user creates.

User can search for event based on the title, description, sign up deadline and event date.

If the user has already signed up or the deadline has passed, a notification will be prompted upon attempt to join the event.

User can view and managed the events they created in Manage My Event.

If an event is canceled by the host, the participants will be notified in My Signups Page, the table of canceled event.
Once the participant acknowledged the canceled event, it will be removed from the view permanently.

Upon sign up, a default profile picture will be generated for the user. The user can change their profile picture after logging in. Note: the profile picture will be clear upon redeployment.

