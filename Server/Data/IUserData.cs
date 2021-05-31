using Server.Models;
using System.Collections.Generic;

namespace Server.Data
{
    public interface IUserData
    {
        List<User> GetUsers();

        User GetUser(int id);

        User AddUser(User user);

        void DeleteUser(User user);

        User EditUser(User user);
    }
}
