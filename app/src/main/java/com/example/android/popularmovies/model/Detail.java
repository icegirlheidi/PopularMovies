package com.example.android.popularmovies.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Detail implements Parcelable {

    private String backdrop_path;

    private List<Genre> genres;

    private int id;

    private String original_title;

    private String overview;

    private String poster_path;

    private String release_date;

    private double vote_average;

    private int runtime;

    private List<Language> spoken_languages;

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public double getVote_average() {
        return vote_average;
    }

    public int getRuntime() {
        return runtime;
    }

    public List<Language> getSpoken_languages() {
        return spoken_languages;
    }

    private Detail(Parcel in) {
        backdrop_path = in.readString();
        id = in.readInt();
        original_title = in.readString();
        overview = in.readString();
        poster_path = in.readString();
        release_date = in.readString();
        vote_average = in.readDouble();
        runtime = in.readInt();
    }

    public static final Creator<Detail> CREATOR = new Creator<Detail>() {
        @Override
        public Detail createFromParcel(Parcel in) {
            return new Detail(in);
        }

        @Override
        public Detail[] newArray(int size) {
            return new Detail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(backdrop_path);
        dest.writeInt(id);
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeString(poster_path);
        dest.writeString(release_date);
        dest.writeDouble(vote_average);
        dest.writeInt(runtime);
    }

    public class Genre {

        private int id;

        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class Language {

        private String iso_639_1;

        private String name;

        public String getIso_639_1() {
            return iso_639_1;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}