public class Date {
    String date;
    int day;
    int month;
    int year;

    public Date(String d) {
        month = Integer.parseInt(d.substring(0,2));
        day = Integer.parseInt(d.substring(3,5));
        year = Integer.parseInt(d.substring(6,8));
    }
    public Date(int m, int d, int y) {
        month = m;
        day = d;
        year = y;
    }
    public int getDay() {
        return day;
    }
    public int getMonth() {
        return month;
    }
    public int getYear() {
        return year;
    }
    public void print() {
        System.out.println(date);
    }
    public String getDotFormat() {
        return String.format("%s.%s.%s",month,day,year);
    }
}
