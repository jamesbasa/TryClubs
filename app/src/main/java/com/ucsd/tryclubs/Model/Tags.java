package com.ucsd.tryclubs.Model;

import java.util.ArrayList;

public class Tags {

    public boolean academic;
    public boolean art;
    public boolean business;
    public boolean comedy;
    public boolean community_service;
    public boolean culture;
    public boolean dance;
    public boolean engineering;
    public boolean ethnic;
    public boolean gaming;
    public boolean greek;
    public boolean lifestyle;
    public boolean martial_arts;
    public boolean media;
    public boolean medical;
    public boolean music;
    public boolean performing_arts;
    public boolean programming;
    public boolean religion;
    public boolean science;
    public boolean self_improvement;
    public boolean social;
    public boolean sports;
    public boolean technology;

    public Tags() {
    }

    public Tags(int custom) {
        this.academic = false;
        this.art = false;
        this.business = false;
        this.comedy = false;
        this.community_service = false;
        this.culture = false;
        this.dance = false;
        this.engineering = false;
        this.ethnic = false;
        this.gaming = false;
        this.greek = false;
        this.lifestyle = false;
        this.martial_arts = false;
        this.media = false;
        this.medical = false;
        this.music = false;
        this.performing_arts = false;
        this.programming = false;
        this.religion = false;
        this.science = false;
        this.self_improvement = false;
        this.social = false;
        this.sports = false;
        this.technology = false;
    }

    public Tags(boolean academic, boolean art, boolean business, boolean comedy, boolean community_service, boolean culture, boolean dance, boolean engineering, boolean ethnic, boolean gaming, boolean greek, boolean lifestyle, boolean martial_arts, boolean media, boolean medical, boolean music, boolean performing_arts, boolean programming, boolean religion, boolean science, boolean self_improvement, boolean social, boolean sports, boolean technology) {
        this.academic = academic;
        this.art = art;
        this.business = business;
        this.comedy = comedy;
        this.community_service = community_service;
        this.culture = culture;
        this.dance = dance;
        this.engineering = engineering;
        this.ethnic = ethnic;
        this.gaming = gaming;
        this.greek = greek;
        this.lifestyle = lifestyle;
        this.martial_arts = martial_arts;
        this.media = media;
        this.medical = medical;
        this.music = music;
        this.performing_arts = performing_arts;
        this.programming = programming;
        this.religion = religion;
        this.science = science;
        this.self_improvement = self_improvement;
        this.social = social;
        this.sports = sports;
        this.technology = technology;
    }

    public boolean isacademic() {
        return academic;
    }

    public void setAcademic(boolean academic) {
        this.academic = academic;
    }

    public boolean isart() {
        return art;
    }

    public void setArt(boolean art) {
        this.art = art;
    }

    public boolean isbusiness() {
        return business;
    }

    public void setBusiness(boolean business) {
        this.business = business;
    }

    public boolean iscomedy() {
        return comedy;
    }

    public void setComedy(boolean comedy) {
        this.comedy = comedy;
    }

    public boolean iscommunity_service() {
        return community_service;
    }

    public void setCommunity_service(boolean community_service) {
        this.community_service = community_service;
    }

    public boolean isculture() {
        return culture;
    }

    public void setCulture(boolean culture) {
        this.culture = culture;
    }

    public boolean isdance() {
        return dance;
    }

    public void setDance(boolean dance) {
        this.dance = dance;
    }

    public boolean isengineering() {
        return engineering;
    }

    public void setEngineering(boolean engineering) {
        this.engineering = engineering;
    }

    public boolean isethnic() {
        return ethnic;
    }

    public void setEthnic(boolean ethnic) {
        this.ethnic = ethnic;
    }

    public boolean isgaming() {
        return gaming;
    }

    public void setGaming(boolean gaming) {
        this.gaming = gaming;
    }

    public boolean isgreek() {
        return greek;
    }

    public void setGreek(boolean greek) {
        this.greek = greek;
    }

    public boolean islifestyle() {
        return lifestyle;
    }

    public void setLifestyle(boolean lifestyle) {
        this.lifestyle = lifestyle;
    }

    public boolean ismartial_arts() {
        return martial_arts;
    }

    public void setMartial_arts(boolean martial_arts) {
        this.martial_arts = martial_arts;
    }

    public boolean ismedia() {
        return media;
    }

    public void setMedia(boolean media) {
        this.media = media;
    }

    public boolean ismedical() {
        return medical;
    }

    public void setMedical(boolean medical) {
        this.medical = medical;
    }

    public boolean ismusic() {
        return music;
    }

    public void setMusic(boolean music) {
        this.music = music;
    }

    public boolean isperforming_arts() {
        return performing_arts;
    }

    public void setPerforming_arts(boolean performing_arts) {
        this.performing_arts = performing_arts;
    }

    public boolean isprogramming() {
        return programming;
    }

    public void setProgramming(boolean programming) {
        this.programming = programming;
    }

    public boolean isreligion() {
        return religion;
    }

    public void setReligion(boolean religion) {
        this.religion = religion;
    }

    public boolean isscience() {
        return science;
    }

    public void setScience(boolean science) {
        this.science = science;
    }

    public boolean isself_improvement() {
        return self_improvement;
    }

    public void setSelf_improvement(boolean self_improvement) {
        this.self_improvement = self_improvement;
    }

    public boolean issocial() {
        return social;
    }

    public void setSocial(boolean social) {
        this.social = social;
    }

    public boolean issports() {
        return sports;
    }

    public void setSports(boolean sports) {
        this.sports = sports;
    }

    public boolean istechnology() {
        return technology;
    }

    public void setTechnology(boolean technology) {
        this.technology = technology;
    }

    public ArrayList<String> allTrue() {
        ArrayList<String> toReturn = new ArrayList<>();
        if (academic) toReturn.add("#academic");
        if (art) toReturn.add("#art");
        if (business) toReturn.add("#business");
        if (comedy) toReturn.add("#comedy");
        if (community_service) toReturn.add("#community_service");
        if (culture) toReturn.add("#culture");
        if (dance) toReturn.add("#dance");
        if (engineering) toReturn.add("#engineering");
        if (ethnic) toReturn.add("#ethnic");
        if (gaming) toReturn.add("#gaming");
        if (greek) toReturn.add("#greek");
        if (lifestyle) toReturn.add("#lifestyle");
        if (martial_arts) toReturn.add("#martial_arts");
        if (media) toReturn.add("#media");
        if (medical) toReturn.add("#medical");
        if (music) toReturn.add("#music");
        if (performing_arts) toReturn.add("#performing_arts");
        if (programming) toReturn.add("#programming");
        if (religion) toReturn.add("#religion");
        if (science) toReturn.add("#science");
        if (self_improvement) toReturn.add("#self_improvement");
        if (social) toReturn.add("#social");
        if (sports) toReturn.add("#sports");
        if (technology) toReturn.add("#technology");
        return toReturn;
    }
}
