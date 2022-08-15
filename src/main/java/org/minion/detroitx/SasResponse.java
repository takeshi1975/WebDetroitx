package org.minion.detroitx;

import lombok.Getter;
import lombok.Setter;

/**
 * Response from SAS with two different tokens
 * @author jjalejandro
 *
 */
@Getter
@Setter		
public class SasResponse {
	/** 
	 * Corporative token
	 */
	private String tokenCorp;
	/**
	 * JWT token
	 */
	private String jwt;
}
