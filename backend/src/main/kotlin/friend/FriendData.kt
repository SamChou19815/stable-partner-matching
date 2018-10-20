package friend

import auth.GoogleUser

/**
 * [FriendData] is a combination of friend list and requests for the given user.
 *
 * @property list a list of friends.
 * @property requests a list of friend requests.
 */
class FriendData private constructor(
        private val list: Set<GoogleUser>,
        private val requests: List<GoogleUser>
) {

    /**
     * Construct the [FriendData] for the given [user].
     */
    constructor(user: GoogleUser): this(list = FriendPair[user], requests = FriendRequest[user])

}
