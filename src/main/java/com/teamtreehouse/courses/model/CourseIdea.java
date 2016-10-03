package com.teamtreehouse.courses.model;

import com.github.slugify.Slugify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CourseIdea {
    private final String title;
    private final String creator;
    private final String slug;
    private Set<String> voters;

    public CourseIdea(String title, String creator) {
        this.title = title;
        this.creator = creator;
        Slugify slugify = new Slugify();
        slug = slugify.slugify(title);
        voters = new HashSet<>();
    }

    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator;
    }

    public String getSlug() {
        return slug;
    }

    public boolean addVoter(String voterUserName) {
        return voters.add(voterUserName);
    }

    public int getVoteCount() {
        return voters.size();
    }

    public List<String> getVoters() {
        return new ArrayList<>(voters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CourseIdea courseId = (CourseIdea) o;

        if (title != null ? !title.equals(courseId.title) : courseId.title != null) return false;
        return creator != null ? creator.equals(courseId.creator) : courseId.creator == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CourseIdea{" +
                "title='" + title + '\'' +
                ", creator='" + creator + '\'' +
                '}';
    }
}
