# MyBudget

# Description
MyBudget is an app meant to be used for tracking our spendings with ease, implementing MPAndroidChart for quick visualization of our movements

/-------------------/

# Tutorial
First of all you should register to enter the app, introduce your data into "Email" and "Password" fields and then click "Sign up".
The app itself creates a new account for you but if you click on "New account" you can either generate a new random account (if you don't really care about having a non-accurate one) or introduce IBAN and balance (by default it'll be 0).

Now you can select which account you want to visualize or delete, once you have selected the account, click "View account" to see your account current status.
From here you can see your balance at top, a Pie Chart with your last 30 days movements (if there's none, it'll be empty), and the options for movements.

If you want to create a new movement, click on "New movement", introduce quantity, date (this field is optional, leave it empty for set the date as today's) and type of movement (this can be seen as where/why you did this movement). Select if it's an income (you save money) or withdraw (you spend money) then click "Submit".

Therefore we can go back and watch the Pie Chart (shouldn't be empty now). If you want more information about your movements, click "View movements".

Here you can see a list of your movements and filters option. For filters to work just select which filter you want to apply (top bar) then fulfil the fields with the required data, once you have selected the filter click "Apply filters". To retrieve the list of your movements without any filter (see all of them like at the beginning), click "Reset filters".

/-------------------/

# Settings and versions used for the project
- Android version and library: 7.4.2
- Minimum SDK: 32
- Targeted SDK: 33
- Google services Gradle plugin: 4.3.15
- Firebase implementation: 20.0.4
- Firebase implementation platform: 31.3.0
- MPAndroidChart: v3.1.0

/-------------------/

# Phone used for testing
- Pixel 5
- Screen size: 6 inches
- Resolution: 1080x2340px
- Density: 440dpi
- System Image: Sv2
- SDK: 32
- Android: 12L
