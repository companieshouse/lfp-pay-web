package uk.gov.companieshouse.web.lfp.models;

import uk.gov.companieshouse.session.Session;

public interface IUserModel {



    /**
     * Model to transfer user details between controller and view.
     */


        /**
         * Returns the selected unique identifer for this user.
         *
         * @return current user id.
         * @throws IllegalAccessException if user not logged in.
         */
        String getId() throws IllegalAccessException;

        /**
         * Return the email address for this user.
         *
         * @return current user email address.
         * @throws IllegalAccessException if user not logged in.
         */
        String getEmail() throws IllegalAccessException;

        /**
         * Checks if user is logged in.
         *
         * @return <code>true</code> if user logged in, otherwise <code>false</code>.
         */
        boolean isSignedIn();

        /**
         * Method used to update user model with values taken from session context.
         *
         * @return this <code>IUserModel</code>.
         */
        IUserModel populateUserDetails(Session session);

        /**
         * Method used to signout and invalidate this IUserModel.
         *
         * @return this <code>IUserModel</code>.
         */
        IUserModel clear();
    }


