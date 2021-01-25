package com.j_nel.miniatureimdb;

import java.util.List;

public class FilmContentModel {
    private String strTitle;
    private String strRated;
    private String strReleased;
    private String strRuntime;
    private String strGenre;
    private String strDirector;
    private String strActors;
    private String strPlot;
    private String strLanguage;
    private String strPoster;
    private List<Rating> lstRatings = null;
    private String strType;
    private String strYear;
    private String strImdbID;

    //Default constructor
    public FilmContentModel() {
    }

    /**
     * Constructor to populate the film list.
     * @param strTitle
     * @param strYear
     * @param strImdbID
     * @param strType
     * @param strPoster
     */
    public FilmContentModel(String strTitle, String strYear, String strImdbID, String strType, String strPoster) {
        super();
        this.strTitle = strTitle;
        this.strYear = strYear;
        this.strImdbID = strImdbID;
        this.strType = strType;
        this.strPoster = strPoster;
    }

    /**
     * Constructor view the selected film's information.
     * @param strLanguage
     * @param strType
     * @param strPlot
     * @param lstRatings
     * @param strGenre
     * @param strReleased
     * @param strDirector
     * @param strRuntime
     * @param strRated
     * @param strActors
     * @param strPoster
     */
    public FilmContentModel(String strTitle, String strRated, String strReleased, String strRuntime, String strGenre, String strDirector, String strActors, String strPlot, String strLanguage, String strPoster, List<Rating> lstRatings, String strType) {
        super();
        this.strTitle = strTitle;
        this.strRated = strRated;
        this.strReleased = strReleased;
        this.strRuntime = strRuntime;
        this.strGenre = strGenre;
        this.strDirector = strDirector;
        this.strActors = strActors;
        this.strPlot = strPlot;
        this.strLanguage = strLanguage;
        this.strPoster = strPoster;
        this.lstRatings = lstRatings;
        this.strType = strType;
    }

    public String getTitle() {
        return strTitle;
    }

    public void setTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public String getRated() {
        return strRated;
    }

    public void setRated(String strRated) {
        this.strRated = strRated;
    }

    public String getReleased() {
        return strReleased;
    }

    public void setReleased(String strReleased) {
        this.strReleased = strReleased;
    }

    public String getRuntime() {
        return strRuntime;
    }

    public void setRuntime(String strRuntime) {
        this.strRuntime = strRuntime;
    }

    public String getGenre() {
        return strGenre;
    }

    public void setGenre(String strGenre) {
        this.strGenre = strGenre;
    }

    public String getDirector() {
        return strDirector;
    }

    public void setDirector(String strDirector) {
        this.strDirector = strDirector;
    }

    public String getActors() {
        return strActors;
    }

    public void setActors(String strActors) {
        this.strActors = strActors;
    }

    public String getPlot() {
        return strPlot;
    }

    public void setPlot(String strPlot) {
        this.strPlot = strPlot;
    }

    public String getLanguage() {
        return strLanguage;
    }

    public void setLanguage(String strLanguage) {
        this.strLanguage = strLanguage;
    }

    public String getPoster() {
        return strPoster;
    }

    public void setPoster(String strPoster) {
        this.strPoster = strPoster;
    }

    public List<Rating> getlstRatings() {
        return lstRatings;
    }

    public void setlstRatings(List<Rating> lstRatings) {
        this.lstRatings = lstRatings;
    }

    public String getType() {
        return strType;
    }

    public void setType(String strType) {
        this.strType = strType;
    }

    public String getYear() {
        return strYear;
    }

    public void setYear(String strYear) {
        this.strYear = strYear;
    }

    public String getImdbID() {
        return strImdbID;
    }

    public void setImdbID(String strImdbID) {
        this.strImdbID = strImdbID;
    }
}

class Rating {

    private String strSource;
    private String strValue;

    //Default constructor
    public Rating() {
    }

    /**
     * Constructor used to populate Rating's list.
     * @param strSource
     * @param strValue
     */
    public Rating(String strSource, String strValue) {
        super();
        this.strSource = strSource;
        this.strValue = strValue;
    }

    public String getSource() {
        return strSource;
    }

    public void setSource(String strSource) {
        this.strSource = strSource;
    }

    public String getValue() {
        return strValue;
    }

    public void setValue(String strValue) {
        this.strValue = strValue;
    }
}