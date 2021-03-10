package p1;
import java.util.*;
import java.io.*;
import java.sql.*;
class Movie{
	private int movieId;
	private String name;
	private String category;
	private String language;
	private String releaseDate;
	private List<String> casting;
	private double ratings;
	private double totalBusinessDone;
	public int getMovieId() {
		return movieId;
	}
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	public List<String> getCasting() {
		return casting;
	}
	public void setCasting(List<String> casting) {
		this.casting = casting;
	}
	public double getRatings() {
		return ratings;
	}
	public void setRatings(double ratings) {
		this.ratings = ratings;
	}
	public double getTotalBusinessDone() {
		return totalBusinessDone;
	}
	public void setTotalBusinessDone(double totalBusinessDone) {
		this.totalBusinessDone = totalBusinessDone;
	}
	public String toString() {
		return Integer.toString(this.getMovieId())+" - "+this.getName()+" - "+this.getCategory()+" - "+this.getLanguage()+" - "+this.getReleaseDate()+" - "+this.getCasting()+" - "+Double.toString(this.getRatings())+" - "+Double.toString(this.getTotalBusinessDone());
	}
	public static List<Movie> populateMovies(File f){
		Movie m;
		List<Movie>movieList = new ArrayList<Movie>();
		try {
			FileInputStream fi = new FileInputStream(f);
			byte[] arr = new byte[(int)f.length()];
			fi.read(arr);
			String s = new String(arr);
			String[] count = s.split("\n");
			for(int i=0;i<count.length;i++) {
				m=new Movie();
				String data[]=count[i].split(",");
				m.setMovieId(Integer.parseInt(data[0]));
				m.setName(data[1]);
				m.setCategory(data[2]);
				m.setLanguage(data[3]);
				m.setReleaseDate(data[4]);
				String[] c = data[5].split("-");
				List<String>cast = new ArrayList<String>();
				for(int j=0;j<c.length;j++) {
					cast.add(c[j]);
				}
				m.setCasting(cast);
				m.setRatings(Double.parseDouble(data[6]));
				m.setTotalBusinessDone(Double.parseDouble(data[7]));
				movieList.add(m);
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
		return movieList;
	}
	public static boolean allMoviesInDb(List<Movie> ls){
		boolean flag = false;
		int t1 =0,t2=0;
		Movie m;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/movie","root","1234");
			for(int i=0;i<ls.size();i++) {
				m=ls.get(i);
				PreparedStatement ps =con.prepareStatement("Insert into movies values (?,?,?,?,?,?,?)");
				ps.setInt(1, m.getMovieId());
				ps.setString(2, m.getName());
				ps.setString(3, m.getCategory());
				ps.setString(4, m.getLanguage());
				ps.setString(5, m.getReleaseDate());
				ps.setDouble(6, m.getRatings());
				ps.setDouble(7, m.getTotalBusinessDone());
				t1 = ps.executeUpdate();
				List<String>cast = m.getCasting();
				for(int j=0;j<cast.size();j++) {
					PreparedStatement ps1 = con.prepareStatement("Insert into casting values (?,?)");
					ps1.setInt(1,m.getMovieId());
					ps1.setString(2, cast.get(j).trim());
					t2=ps1.executeUpdate();
				}
				if(t1!=0&&t2!=0) {
					flag = true;
				}
			}
		}catch(ClassNotFoundException|SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
		
}
	

class MovieTest {
	static List<Movie> movieList;
	
	public static void main(String[] args) {
		movieList=Movie.populateMovies(new File("C:\\Users\\NARENDRA\\git\\Assignment09_Narendra_Rajoirya\\Ass09_Narendra_Rajoriya\\TextFiles\\movies.txt"));
		Iterator<Movie> it = movieList.iterator();
		while(it.hasNext()) {
			System.out.println(it.next());
		}
		boolean b =Movie.allMoviesInDb(movieList);
		if(b==true) {
			System.out.println("Inserted");
		}else {
			System.out.println("Insertion failed");
		}
	}
	
}
