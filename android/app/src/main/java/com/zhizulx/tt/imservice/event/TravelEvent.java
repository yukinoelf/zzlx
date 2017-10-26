package com.zhizulx.tt.imservice.event;

/**
 * @author : yingmu on 15-1-6.
 * @email : yingmu@mogujie.com.
 */
public class TravelEvent {
    public Event event;
    public TravelEvent(){}
    public TravelEvent(Event e){
        this.event = e;
    }

    public enum Event {
        TRAVEL_LIST_OK,
        TRAVEL_LIST_FAIL,
        CREATE_TRAVEL_OK,
        CREATE_TRAVEL_FAIL,
        DEL_TRAVEL_OK,
        DEL_TRAVEL_FAIL,
        REQ_TRAVEL_ROUTE_OK,
        REQ_TRAVEL_ROUTE_FAIL,
        QUERY_RANDOM_ROUTE_FAIL,
        QUERY_RANDOM_ROUTE_TAG_OK,
        QUERY_RANDOM_ROUTE_SENTENCE_OK,
        UPDATE_RANDOM_ROUTE_OK,
        UPDATE_RANDOM_ROUTE_FAIL,
        CREATE_ROUTE_OK,
        CREATE_ROUTE_FAIL,
        CREATE_COLLECT_ROUTE_OK,
        CREATE_COLLECT_ROUTE_FAIL,
        QUERY_COLLECT_ROUTE_OK,
        QUERY_COLLECT_ROUTE_FAIL,
        DELETE_COLLECT_ROUTE_OK,
        DELETE_COLLECT_ROUTE_FAIL,
        QUERY_SIGHT_HOTEL_OK,
        QUERY_SIGHT_HOTEL_FAIL
    }

    public Event getEvent() {
        return event;
    }
}
