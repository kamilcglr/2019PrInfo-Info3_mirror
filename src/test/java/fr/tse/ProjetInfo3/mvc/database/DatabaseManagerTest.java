/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.database;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.DatabaseManager;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Testing methods OF DB
 *
 */
public class DatabaseManagerTest {

	private DatabaseManager databaseManager;
	private User user;
	private Hashtag hashtag;
	private InterestPoint interestPoint;
	RequestManager requestManager;

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#DatabaseManager()}.
	 */

	@BeforeAll
	public void initialize() {
		databaseManager = new DatabaseManager();
		requestManager = new RequestManager();
		hashtag = new Hashtag("Hashtag");
		interestPoint = new InterestPoint("test", "description interest point", new Date());
	}

	@Test
	public void testDatabaseManager() {
		Assertions.assertNotNull(databaseManager.getInstanceOfGson());
	}

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#cacheUserToDataBase(fr.tse.ProjetInfo3.mvc.dto.User)}.
	 */
	@Test
	public void testCacheUserToDataBase() {
		try {
			user = requestManager.getUser("TahaAlamIdrissi");
			databaseManager.cacheUserToDataBase(user);
			assertEquals(user, databaseManager.getCachedUserFromDatabase("TahaAlamIdrissi"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#cacheHashtagToDataBase(fr.tse.ProjetInfo3.mvc.dto.Hashtag)}.
	 */
	@Test
	public void testCacheHashtagToDataBase() {
		databaseManager.cacheHashtagToDataBase(hashtag);
		assertEquals(hashtag.getHashtag(), databaseManager.getCachedHashtagFromDatabase("Hashtag").getHashtag());
	}

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#cachedUserInDataBase(java.lang.String)}.
	 */
	@Test
	public void testCachedUserInDataBase() {
		assertEquals(true, databaseManager.cachedUserInDataBase("TahaAlamIdrissi"));
	}

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#cachedHashtagInDataBase(java.lang.String)}.
	 */
	@Test
	public void testCachedHashtagInDataBase() {
		assertEquals(true, databaseManager.cachedHashtagInDataBase("Hashtag"));
	}

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#deleteCachedUserFromDataBase(java.lang.String)}.
	 */
	@Test
	public void testDeleteCachedUserFromDataBase() {
		databaseManager.deleteCachedUserFromDataBase("TahaAlamIdrissi");
		assertEquals(false, databaseManager.cachedUserInDataBase("TahaAlamIdrissi"));
	}

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#deleteCachedHashtagFromDatabase(java.lang.String)}.
	 */
	@Test
	public void testDeleteCachedHashtagFromDatabase() {
		databaseManager.deleteCachedHashtagFromDatabase("Hashtag");
		assertEquals(false, databaseManager.cachedHashtagInDataBase("Hashtag"));
	}

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#saveInterestPointToDataBase(fr.tse.ProjetInfo3.mvc.dto.InterestPoint)}.
	 */
	@Test
	public void testSaveInterestPointToDataBase() {

		assertEquals(interestPoint.getId(), databaseManager.saveInterestPointToDataBase(interestPoint));
	}

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#getAllInterestPointFromDataBase(int)}.
	 */
	@Test
	public void testGetAllInterestPointFromDataBase() {
		assertTrue(databaseManager.getAllInterestPointFromDataBase((int)user.getId()).size() > 0);
	}


	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#createFavorites(int)}.
	 */
	@Test
	public void testCreateFavorites() {
		databaseManager.createFavorites((int) user.getId());
	}

	
}
