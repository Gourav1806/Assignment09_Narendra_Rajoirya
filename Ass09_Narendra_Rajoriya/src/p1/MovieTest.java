package p1;
import java.util.*;
import java.io.*;
import java.sql.*;
class Movie implements Serializable,Comparable<Movie>{
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
	public int compareTo(Movie m) {
		int res=-1;
		int i1=this.getMovieId();
		int i2 = m.getMovieId();
		if(i1>i2) {
			res=1;
		}else if(i1<i2) {
			res=-1;
		}else {
			res=0;
		}
		return res;
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
	public static void addMovie(Movie movie,List<Movie>movieList) {
		movieList.add(movie);
	}
	public static void serializeMovies(List<Movie> ls,String fileName) {
		File f = new File(fileName);
		
		try {
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(ls);
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("unchecked")
	public static List<Movie> deserializeMovie(String fileName){
		List<Movie> ls = new ArrayList<Movie>();
		Movie movie=null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
			ls=(ArrayList<Movie>)ois.readObject();
			ois.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ls;
	}
	
}
	

class MovieTest {
	static List<Movie> movieList;
	public static List<Movie> getMoviesReleasedInYear(int year){
		List<Movie> m = new ArrayList<Movie>();
		for(Movie ms : movieList) {
			if(year == Integer.parseInt(ms.getReleaseDate().toString().substring(0,4))) {
				m.add(ms);
			}
		}
		return m;
	}
	public static List<Movie> getMoviesByActor(String... actorNames){
		List <Movie> m=new ArrayList<Movie>();
		for (Movie ms: movieList)
		{
			for (String st:ms.getCasting())
			{
				
				if(Arrays.asList(actorNames).contains(st))
				{
					m.add(ms);
					break;
				}
			}
		}
		return m;
	}
	static void updateMovieRatings(Movie movie,double ratings,List<Movie> movieList) {
		for (Movie m:movieList)
		{
			if(m.equals(movie))
			{
				m.setRatings(ratings);
			}
		}
	}
	static void updateBusiness(Movie movie,double amount,List<Movie> movieList) {
		for(Movie m: movieList) {
			if(m.equals(movie)) {
				m.setTotalBusinessDone(amount);
			}
		}
	}
	static void displayMovieList(List<Movie> movieList) {
		Iterator<Movie> it = movieList.iterator();
		while(it.hasNext()) {
			System.out.println(it.next());
		}
	}
	
	static Map<String,Set<Movie>> businessDone(double amount)
	{
		Set<Movie> bus=new TreeSet<Movie>();
		Map<String,Set<Movie>> map=new HashMap<String,Set<Movie>>();
		for (Movie m:movieList)
		{
			if(m.getTotalBusinessDone()>amount)
			{
				
				bus.add(m);
				if(map.containsKey(m.getLanguage()))
				{
					map.get(m.getLanguage()).add(m);
				}
				else {
				Set<Movie> bus2=new TreeSet<Movie>();
				bus2.add(m);
				map.put(m.getLanguage(),bus2);	
				}
			}
			
		}
		
		for(Map.Entry m : map.entrySet()){    
			    System.out.println(m.getKey()+" "+m.getValue());    
		}  
	
		return map;
		
		
	}
	public static void main(String[] args) {
		Movie m = new Movie();
		m.setMovieId(4);
		m.setName("Three Idiot");
		m.setCategory("comedy");
		m.setLanguage("Hindi");
		m.setReleaseDate("2015-06-13");
		String[] arr={"Aamir Khan","Kareena Kapur"};
		m.setCasting(Arrays.asList(arr));
		m.setRatings(3.5);
		m.setTotalBusinessDone(100);
		movieList=Movie.populateMovies(new File("C:\\Users\\NARENDRA\\git\\Assignment09_Narendra_Rajoirya\\Ass09_Narendra_Rajoriya\\TextFiles\\movies.txt"));
		Movie.addMovie(m, movieList);
		displayMovieList(movieList);
		/*
		Movie.serializeMovies(movieList, "C:\\Users\\NARENDRA\\git\\Assignment09_Narendra_Rajoirya\\Ass09_Narendra_Rajoriya\\TextFiles\\serialize_movie.txt");
		List<Movie> l = Movie.deserializeMovie("C:\\Users\\NARENDRA\\git\\Assignment09_Narendra_Rajoirya\\Ass09_Narendra_Rajoriya\\TextFiles\\serialize_movie.txt");
		displayMovieList(l);
		*/
		/*
		boolean b =Movie.allMoviesInDb(movieList);
		if(b==true) {
			System.out.println("Inserted");
		}else {
			System.out.println("Insertion failed");
		}*/
		/*
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~");
		List<Movie> yl = getMoviesReleasedInYear(2019);
		displayMovieList(yl);
		
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~");
		List<Movie> al = getMoviesByActor("Prabhas","Tammana");
		displayMovieList(al);
		*/
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		updateMovieRatings(m, 3.0, movieList);
		displayMovieList(movieList);
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		updateBusiness(m, 150, movieList);
		displayMovieList(movieList);
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		businessDone(100);
	}
	
}
