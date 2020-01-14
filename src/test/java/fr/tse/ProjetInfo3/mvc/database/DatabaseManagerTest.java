/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.database;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.DatabaseManager;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager.RequestManagerException;

/**
 * 
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

	@Before
	public void initialize() {
		databaseManager = new DatabaseManager();
		requestManager = new RequestManager();
		hashtag = new Hashtag("Hashtag");
		interestPoint = new InterestPoint("test", "description interest point", new Date("dd/mm/yyyy"));
	}

	@Test
	public void testDatabaseManager() {
		assertNotNull(databaseManager.getInstanceOfGson());
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
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#deleteSelectedInterestPointById(long)}.
	 */
	@Test
	public void testDeleteSelectedInterestPointById() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#getTweetsFromInterestPoint(long)}.
	 */
	@Test
	public void testGetTweetsFromInterestPoint() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#setTweetsToInterestPoint(long, java.util.List)}.
	 */
	@Test
	public void testSetTweetsToInterestPoint() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#deleteTweetsFromInterestPoint(long)}.
	 */
	@Test
	public void testDeleteTweetsFromInterestPoint() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#createFavorites(int)}.
	 */
	@Test
	public void testCreateFavorites() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#updateFavouritesInDatabase(fr.tse.ProjetInfo3.mvc.dto.Favourites)}.
	 */
	@Test
	public void testUpdateFavouritesInDatabase() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#getFavourite(int)}.
	 */
	@Test
	public void testGetFavourite() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#addNewUserToDataBase(fr.tse.ProjetInfo3.mvc.dto.UserApp)}.
	 */
	@Test
	public void testAddNewUserToDataBase() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.tse.ProjetInfo3.mvc.repository.DatabaseManager#getUserFromDataBase(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetUserFromDataBase() {
		fail("Not yet implemented");
	}

}
