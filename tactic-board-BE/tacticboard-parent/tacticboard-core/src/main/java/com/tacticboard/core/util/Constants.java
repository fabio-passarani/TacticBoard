package com.tacticboard.core.util;

public final class Constants {
    
    // General constants
    public static final String APP_NAME = "TacticBoard";
    public static final String API_VERSION = "v1";
    public static final String API_BASE_PATH = "/api/" + API_VERSION;
    
    // Authentication constants
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 86400; // 24 hours
    
    // Role constants
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_COACH = "ROLE_COACH";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    
    // File storage constants
    public static final String PROFILE_PICTURES_DIR = "profile-pictures";
    public static final String TEAM_LOGOS_DIR = "team-logos";
    public static final String PLAYER_PHOTOS_DIR = "player-photos";
    public static final String VIDEOS_DIR = "videos";
    public static final String EXERCISE_DIAGRAMS_DIR = "exercise-diagrams";
    public static final String TACTIC_THUMBNAILS_DIR = "tactic-thumbnails";
    
    // Subscription limits
    public static final int FREE_PLAN_MAX_TEAMS = 1;
    public static final int BASIC_PLAN_MAX_TEAMS = 3;
    public static final int PREMIUM_PLAN_MAX_TEAMS = 10;
    public static final int PROFESSIONAL_PLAN_MAX_TEAMS = 25;
    
    public static final int FREE_PLAN_STORAGE_GB = 1;
    public static final int BASIC_PLAN_STORAGE_GB = 5;
    public static final int PREMIUM_PLAN_STORAGE_GB = 20;
    public static final int PROFESSIONAL_PLAN_STORAGE_GB = 100;
    
    // Email templates
    public static final String WELCOME_EMAIL_SUBJECT = "Welcome to TacticBoard";
    public static final String PASSWORD_RESET_EMAIL_SUBJECT = "Reset Your TacticBoard Password";
    public static final String SUBSCRIPTION_CONFIRMATION_SUBJECT = "TacticBoard Subscription Confirmation";
    
    // Prevent instantiation
    private Constants() {
        throw new AssertionError("Constants class should not be instantiated");
    }
}