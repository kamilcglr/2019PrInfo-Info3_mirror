/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.repository;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.mgiorda.oauth.HttpMethod;
import com.mgiorda.oauth.OAuthConfig;
import com.mgiorda.oauth.OAuthConfigBuilder;
import com.mgiorda.oauth.OAuthSignature;



/**
 * @author Laïla
 *
 */
public class OAuthSample {

	  public static void main(String[] args) {
			OAuthConfig oauthConfig = new OAuthConfigBuilder("PahWHDFSZ02bTaqFUVamZ0iBI", "mGDqU2cwWrw85cMvj7YOBSczI8qZQM0IKKymdbRL82sXqtyhhr")
					.setTokenKeys("4664421557-y8N6WL3BVrhBTIfuzZcHqRmNZeGDkt0TbAFoz9g", "riGJEs4QhZWjOgQyQJY4jQJM8nCRnsfHisU1Vnq1VpDiv")
					.build();

			OAuthSignature signature = oauthConfig.buildSignature(HttpMethod.GET, "https://api.twitter.com/oauth1")
					.addQueryParam("aParam", "aValue")
					.addFormUrlEncodedParam("myParam", "anotherValue")
					.create();

		//	System.out.println(signature.getAsHeader());
	  }
}
