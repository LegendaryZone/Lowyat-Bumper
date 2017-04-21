# Lowyat-Bumper
Lowyat Bumper is an Android app that sends HTTPS requests to https://forum.lowyat.net to login, retrieve and bump topics. It also enable users to schedule a time and day to send these requests.

**Important Note**
On some Android devices, "force closing" the app or rebooting will terminate all processes associated with the app (including scheduled bumps) and will not start until you choose to manually do so. This is NOT a bug, it is an Android security feature. If so, just start the app again and don't "force close" it this time.

**How to Use?**
First, login at the main screen by entering your Lowyat forum username and password. Then, tap the blue round login button to start retrieving posts. It will show you a list of topics that can be bumped. Select any number of topics and tap the "Bump" button to bump them immediately OR tap the "Schedule" button to set it to automatically bump on a desired time and day. To delete or view your list of scheduled bumps, simply tap "Schedule" without selecting any topics.

Google Play Store
----------------------
The official link for the app is at https://play.google.com/store/apps/details?id=com.railkill.lowyatbumper. It is now free and open source.
When there are enough changes for a milestone here, I will update the signed .apk in the Google Play Store. Otherwise, you will have to build the .apk yourself.

FAQ
--------

**Q:** Is this illegal or against the rules?

**A:** No.

**Q:** Why can't I login? All it says is "Unable to retrieve posts."

**A:** Lowyat forums only allow topics in the Trade Zone or Classifieds section to be bumped. If you have no posts in those categories, this app will not do anything. If you do, make sure you are connected to the Internet and that your login details are correct.

**Q:** It says "Bump request sent." but why isn't my topic bumped?

**A:** You cannot bump topics more than once per day.

**Q:** How do I forget my username and password?

**A:** This app encrypts and saves your login details on your phone because it is required for scheduled bumps. If you do not want this to happen, you may uninstall or clear app data to forget all saved data associated with this app.

**Q:** Why are my schedules tied to the phone? I logged in to another account and my previous scheduled bumps fail.

**A:** This app is designed according to Lowyat forum rules where you are only limited to one account.

**Q:** Why are my scheduled bump timings inexact?

**A:** On some custom ROMs such as Xiaomi's MiUI with energy saving features, scheduled timings will not be accurate. This is also true for Android KitKat (API 19) and above, but there are functions the app uses in Android KitKat and above which will set the schedule as accurately as possible.
