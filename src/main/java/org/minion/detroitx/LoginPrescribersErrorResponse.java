package org.minion.detroitx;

import java.io.Serializable;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


/**
 * The Class LoginPrescribersErrorResponse.
 */
@Slf4j
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class LoginPrescribersErrorResponse {

	/** The app name. */
	private String appName;

	/** The time stamp. */
	private Long timeStamp;

	/** The error name. */
	private String errorName;

	/** The status. */
	private Integer status;

	/** The internal code. */
	private Integer internalCode;

	/** The short message. */
	private String shortMessage;

	/** The detailed message. */
	private String detailedMessage;

	/** The map extended message. */
	private Map<String, ? extends Serializable> mapExtendedMessage;

	/**
	 * Instantiates a new login prescribers error response.
	 *
	 * @param appName the app name
	 * @param ex the ex
	 */
	public LoginPrescribersErrorResponse(String appName) {
		this.appName = appName;
		this.timeStamp = System.currentTimeMillis();
		log.error("Error");
	}


	/**
	 * Instantiates a new login prescribers error response.
	 *
	 * @param appName the app name
	 * @param errorName the error name
	 * @param status the status
	 * @param detailedMessage the detailed message
	 * @param mapExtendedMessage the map extended message
	 */
	public LoginPrescribersErrorResponse(String appName, String errorName, HttpStatus status,
			String detailedMessage, Map<String, ? extends Serializable> mapExtendedMessage) {
		this.appName = appName;
		this.timeStamp = System.currentTimeMillis();
		this.errorName = errorName;
		this.status = status.value();
		this.internalCode = status.value();
		this.shortMessage = status.getReasonPhrase();
		this.detailedMessage = detailedMessage;
		this.mapExtendedMessage = mapExtendedMessage;
	}
}
