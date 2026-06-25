package com.maven.rms.models.payload.requests;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UAMRequestPayload {
	@JsonProperty("Header")
	private UAMRequestHeader Header;
	@JsonProperty("Request")
	private UAMRequestRequest Request;

	/*
	public UAMRequestHeader getHeader() {
		return Header;
	}
	public void setHeader(UAMRequestHeader Header) {
		this.Header = Header;
	}
	public UAMRequestRequest getRequest() {
		return Request;
	}
	public void setRequest(UAMRequestRequest Request) {
		this.Request = Request;
	}
	*/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ApiResponse[");
        sb.append("header=").append(Header); // This will call the toString() of the T type
        sb.append("request=").append(Request); // This will call the toString() of the T type
        sb.append("]");
        return sb.toString();
    }
}
