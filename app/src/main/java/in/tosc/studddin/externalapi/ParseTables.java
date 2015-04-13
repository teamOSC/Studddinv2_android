package in.tosc.studddin.externalapi;

/**
 * Created by championswimmer on 5/2/15.
 */
public class ParseTables {

    public static class Users {
        public static final String NAME = "NAME";
        public static final String USERNAME = "USERNAME";
        public static final String PASSWORD = "PASSWORD";
        public static final String DOB = "DOB";
        public static final String INSTITUTE = "INSTITUTE";
        public static final String CITY = "CITY";
        public static final String EMAIL = "email";
        public static final String INTERESTS = "interests";
        public static final String QUALIFICATIONS = "QUALIFICATIONS";
        public static final String LOCATION = "location";
        public static final String IMAGE = "image";
        public static final String FULLY_REGISTERED = "FULLY_REGISTERED";
        public static final String COVER = "cover";
    }

    public static class Listings {
        public static final String LISTINGS = "Listings";
        public static final String IMAGE = "image";
        public static final String OWNER_NAME = "ownerName";
        public static final String LISTING_NAME = "listingName";
        public static final String LISTING_DESC = "listingDesc";
        public static final String MOBILE = "mobile";
        public static final String LOCATION = "location";
        public static final String CATEGORY = "category";
        public static final String LISTING_PNG = "listing.png";
        public static final String CREATED_AT = "createdAt";
    }

    public static class Events {
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String TYPE = "type";
        public static final String LOCATION = "location";
        public static final String LOCATION_DES = "location_des";
        public static final String USER = "user";
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String CREATED_BY = "createdBy";
        public static final String CREATED_AT = "createdAt";
        public static final String EVENT_PNG = "event.png";
        public static final String IMAGE = "image";
        public static final String URL = "url";
        public static final String CONTACT = "contact";
    }

    public static class Interests {
        public static final String _NAME = "Interests";
        public static final String NAME = "name";
        public static final String USERS = "users";
    }

    public static class College {
        public static final String _NAME = "College";
        public static final String NAME = "name";
    }

    public static class People {
        public static final String PEOPLE_NEAR_ME= "PeopleNearMe";
        public static final String PEOPLE_SAME_INSTITUTE= "PeopleSameInstitute";
        public static final String PEOPLE_SAME_INTERESTS= "PeopleSameInterests";
        public static final String PEOPLE_USER_INTERESTS= "CurrentUserInterests";


    }

}
