package com.mistminds.web.rest.util;

import org.springframework.http.HttpHeaders;

/**
 * Utility class for http header creation.
 *
 */
public class HeaderUtil {

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-reachoutApp-alert", message);
        headers.add("X-reachoutApp-params", param);
        return headers;
    }

    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert("reachoutApp." + entityName + ".created", param);
    }

    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert("reachoutApp." + entityName + ".updated", param);
    }
    public static HttpHeaders createEntitySentAlert(String entityName, String param) {
        return createAlert("reachoutApp." + entityName + ".sent", param);
    }
    public static HttpHeaders createEntityFailureAlert(String entityName, String param) {
        return createAlert("reachoutApp." + entityName + ".failure", param);
    }
    public static HttpHeaders createEntityBlockAlert(String entityName, String param) {
        return createAlert("reachoutApp." + entityName + ".blocked", param);
    }
    
    public static HttpHeaders createEntityActivateAlert(String entityName, String param) {
        return createAlert("reachoutApp." + entityName + ".activated", param);
    }

    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert("reachoutApp." + entityName + ".deleted", param);
    }
    public static HttpHeaders rollBackAlert(String entityName, String param) {
        return createAlert("reachoutApp." + entityName + ".rollback", param);
    }
    public static HttpHeaders createEntityGetAlert(String entityName, String param) {
        return createAlert("reachoutApp." + entityName + ".credited", param);
    }

    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-reachoutApp-error", "error." + errorKey);
        headers.add("X-reachoutApp-params", entityName);
        return headers;
    }

	public static HttpHeaders createEntityGetAler(String provider, String id) {
		// TODO Auto-generated method stub
		return null;
	}
}
