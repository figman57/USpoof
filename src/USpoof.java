import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Ian Anderson
 * 7/24/19
 */

public class USpoof {
    OkHttpClient client;
    String user;
    String pass;
    String nid;
    String platform;
    String uuid;
    String loginKey = "";
    List<String> eventIDs = new ArrayList<>();
    List<String> eventDescriptions = new ArrayList<>();
    List<String> pointValues = new ArrayList<>();
    List<String> startTimes = new ArrayList<>();
    List<String> endTimes = new ArrayList<>();
    List<LocalDateTime> startLDTs = new ArrayList<>();
    List<Double> latitidues = new ArrayList<>();
    List<Double> longitudes = new ArrayList<>();
    /**
     * USpoof: A SuperFanU Spoofer
     * @param userN username
     * @param passN password
     * @param nidN school id
     * @throws IOException
     */
    public USpoof(String userN, String passN, String nidN) throws IOException
    {
        user = userN;
        pass = passN;
        nid = nidN;
        // nid changes school
        platform = "Android";
        // no idea what determines this
        uuid = "9956be6596eae6cf";
        client = new OkHttpClient();
        Random rand = new Random();
        System.out.println("Welcome to USpoof Beta 1.0\nBy Ian Anderson, 2019");
        logIn();
        getFeed();
        for(int i = 0; i < eventIDs.size(); i++)
        {
            System.out.println("EVENT ID = " + eventIDs.get(i));
            System.out.println("EVENT DESCRIPTION = " + eventDescriptions.get(i));
            System.out.println("POINT VALUE = " + pointValues.get(i));
            System.out.println("START TIME = " + startTimes.get(i));
            System.out.println("END TIME = " + endTimes.get(i));
            Request currentEvent = new Request.Builder()
                    .url("https://api.superfanu.com/7.0.1/event/" + eventIDs.get(i) + "/details")
                    .addHeader("nid", nid)
                    .addHeader("platform", platform)
                    .addHeader("uuid", uuid)
                    .addHeader("login_key", loginKey)
                    .get()
                    .build();
            Response eventResponse = client.newCall(currentEvent).execute();
            //System.out.println(eventResponse.body().string());
            JSONObject eventJSON = new JSONObject(eventResponse.body().string());
            JSONObject data3 = eventJSON.getJSONArray("data").getJSONObject(0).getJSONObject("event").getJSONArray("venues").getJSONObject(0);
            String lattude = data3.getString("latitude");
            String lontude = data3.getString("longitude");
            System.out.println("LATITUDE = " + lattude);
            System.out.println("LONGITUDE = " + lontude);
            double randCloseLattude = ((Math.round(Double.parseDouble(lattude) * 1000) * 100) + rand.nextInt(100)) / 100000.0;
            double randCloseLontude = ((Math.round(Double.parseDouble(lontude) * 1000) * 100) + rand.nextInt(100)) / 100000.0;
            System.out.println("RANDOM CLOSE LATITUDE = " + randCloseLattude);
            System.out.println("RANDOM CLOSE LONGITUDE = " + randCloseLontude);
            System.out.println("EXACT LOCATION PREVIEW = " + "https://www.google.com/maps/search/?api=1&query=" + lattude + "," + lontude);
            System.out.println("RANDOM CLOSE LOCATION PREVIEW = " + "https://www.google.com/maps/search/?api=1&query=" + randCloseLattude + "," + randCloseLontude);
            LocalDateTime startIn = LocalDateTime.parse(startTimes.get(i), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            long millis = Duration.between(LocalDateTime.now(), startIn).toMillis();
            String timeUntil = String.format("%d days, %d hours, %d mins",
                    TimeUnit.MILLISECONDS.toDays(millis),
                            TimeUnit.MILLISECONDS.toHours(millis) -
                            TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis)),
                            TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
            System.out.println("Event starts in " + timeUntil + ".\n");
            startLDTs.add(startIn);
        }
        try
        {
            long millis = 1;
            for(int i = 0; i < eventIDs.size(); i++)
            {
                while (millis > 0)
                {
                    millis = Duration.between(LocalDateTime.now(), startLDTs.get(i)).toMillis();
                    String timeUntil = String.format("%d days, %d hours, %d mins",
                            TimeUnit.MILLISECONDS.toDays(millis),
                                    TimeUnit.MILLISECONDS.toHours(millis) -
                                    TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis)),
                                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
                    System.out.println(timeUntil + " until " + eventDescriptions.get(i));
                    Thread.sleep(60000);
                }
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    private void logIn() throws IOException
    {
        System.out.println("Logging in with username " + user + " at school ID " + nid + "...");
        RequestBody loginToU = new FormBody.Builder()
                .add("user", user)
                .add("pass", pass)
                .add("nid", nid)
                .add("platform", platform)
                .add("uuid", uuid)
                .build();
        Request login = new Request.Builder()
                .url("https://api.superfanu.com/7.0.1/login")
                .post(loginToU)
                .build();
        Response response = client.newCall(login).execute();
        if(response.isSuccessful())
        {
            System.out.println("Login Successful!");
        }
        JSONObject loginResponse = new JSONObject(response.body().string());
        JSONArray data = loginResponse.getJSONArray("data");
        for(int i = 0; i < data.length(); i++)
        {
            loginKey = data.getJSONObject(i).getString("login_key");
        }
    }
    private void getFeed() throws IOException
    {
        System.out.println("Getting feed...");
        Request feed = new Request.Builder()
                .url("https://api.superfanu.com/7.0.1/feed")
                .addHeader("nid", nid)
                .addHeader("platform", platform)
                .addHeader("uuid", uuid)
                .addHeader("login_key", loginKey)
                .get()
                .build();
        Response feedResponse = client.newCall(feed).execute();
        if(feedResponse.isSuccessful())
        {
            System.out.println("Feed successfully fetched!\n");
        }
        JSONObject jsonFeed = new JSONObject(feedResponse.body().string());
        JSONArray data2 = jsonFeed.getJSONArray("data").getJSONArray(0);
        List<JSONObject> events = new ArrayList<>();
        for(int i = 0; i < data2.length(); i++)
        {
            JSONObject current = data2.getJSONObject(i);
            if(current.getString("type").equals("event"))
            {
                events.add(current);
            }
        }
        for(JSONObject e: events)
        {
            eventIDs.add(e.getString("eid"));
            eventDescriptions.add(e.getString("description"));
            pointValues.add(e.getString("pointvalue"));
            startTimes.add(e.getString("starttime"));
            endTimes.add(e.getString("endtime"));
            //System.out.println(e.toString());
        }
    }
    private void checkin(String eventID)
    {

    }
}