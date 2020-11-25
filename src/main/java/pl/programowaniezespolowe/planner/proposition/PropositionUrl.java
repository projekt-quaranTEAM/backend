package pl.programowaniezespolowe.planner.proposition;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PropositionUrl {

    private List<Proposition> li;
    private String url;
    private int activityid;
    private String category;

    public PropositionUrl() {}

    public PropositionUrl(String url, int activityid, String category) {
        this.url = url;
        this.activityid = activityid;
        this.category = category;
        try {
            this.li = getEventsByCategory();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public List<Proposition> getEventsByCategory() throws IOException {
        List<Proposition> li = new ArrayList<>();
        Document doc = Jsoup.connect(this.url).get();
        String title = doc.title();

        System.out.println(title);

        Elements elements = doc.select("div.search-main-content");
        Elements el = elements.select("div.search-event-card-wrapper");

        for (Element e : el) {

            //System.out.println("Event:");
            Element name = e.select("div.eds-is-hidden-accessible").first();
            //System.out.println(name.text());

            Element els2 = e.select("div.eds-text-color--primary-brand.eds-l-pad-bot-1.eds-text-weight--heavy.eds-text-bs").first();
            //System.out.println(els2.text());

            Element links = e.select("a[href]").first();
            String link = links.toString().split(" ")[2].substring(6, links.toString().split(" ")[2].length() - 1);
            //System.out.println(links.toString().split(" ")[2].substring(6, links.toString().split(" ")[2].length() - 1));

            String date = els2.text();
            String [] dates = date.split(" ");
            String month = getMonthByName(dates[1]);
            int day = Integer.valueOf(dates[2].substring(0, dates[2].length() - 1));
            int year = Integer.valueOf(dates[3]);
            int hour = Integer.valueOf(dates[4].split(":")[0]);
            String hourRest = dates[4].split(":")[1];

            if(dates[5].equals("PM")) hour += 12;
            String shift = dates[7];
            if(shift.charAt(1) == '+'){
                if(shift.contains("12")) hour += 12;
                else if(shift.contains("11")) hour += 11;
                else if(shift.contains("10")) hour += 10;
                else if(shift.contains("9")) hour += 9;
                else if(shift.contains("8")) hour += 8;
                else if(shift.contains("7")) hour += 7;
                else if(shift.contains("6")) hour += 6;
                else if(shift.contains("5")) hour += 5;
                else if(shift.contains("4")) hour += 4;
                else if(shift.contains("3")) hour += 3;
                else if(shift.contains("2")) hour += 2;
                else if(shift.contains("1")) hour += 1;
                else  hour += 0;
            }
            else {
                if(shift.contains("12")) hour -= 12;
                else if(shift.contains("11")) hour -= 11;
                else if(shift.contains("10")) hour -= 10;
                else if(shift.contains("9")) hour -= 9;
                else if(shift.contains("8")) hour -= 8;
                else if(shift.contains("7")) hour -= 7;
                else if(shift.contains("6")) hour -= 6;
                else if(shift.contains("5")) hour -= 5;
                else if(shift.contains("4")) hour -= 4;
                else if(shift.contains("3")) hour -= 3;
                else if(shift.contains("2")) hour -= 2;
                else if(shift.contains("1")) hour -= 1;
                else  hour += 0;
            }
            if(hour < 0) {
                hour += 24;
                day -= 1;
            }
            if(hour > 24) {
                hour -= 24;
                day += 1;
            }
            String dayS = "";
            if(day < 10) dayS = "0" + day;
            else dayS = String.valueOf(day);

            //System.out.println(hour + ":" + hourRest + " " + day + " " + month + " " + year);
            String data = dayS + "-" + month + "-" + year + " " + hour + ":" + hourRest + ":00";
            //System.out.println(data);
            Date dt = null;
            try {
                dt = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse(data);
                //System.out.println(dt.toString());
            } catch (ParseException ex) {
                ex.printStackTrace();
            }

            try {
                li.add(new Proposition(name.text(), link, 1, this.activityid, this.category, dt));
            }
            catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            //System.out.println("=======================================");
        }
        return li;
    }
    public String getMonthByName(String mon) {
        if(mon.equals("Jan")) return "01";
        else if(mon.equals("Feb")) return "02";
        else if(mon.equals("Mar")) return "03";
        else if(mon.equals("Apr")) return "04";
        else if(mon.equals("May")) return "05";
        else if(mon.equals("Jun")) return "06";
        else if(mon.equals("Jul")) return "07";
        else if(mon.equals("Aug")) return "08";
        else if(mon.equals("Sep")) return "09";
        else if(mon.equals("Oct")) return "10";
        else if(mon.equals("Nov")) return "11";
        else return "12";
    }

    public List<Proposition> getLi() {
        return li;
    }

    public void setLi(List<Proposition> li) {
        this.li = li;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getActivityid() {
        return activityid;
    }

    public void setActivityid(int activityid) {
        this.activityid = activityid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
