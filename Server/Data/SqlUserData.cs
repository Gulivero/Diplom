using Server.Models;
using Server.Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Server.Data
{
    public class SqlUserData : IUserData
    {
        private UserContext _userContext;
        public SqlUserData(UserContext userContext)
        {
            _userContext = userContext;
        }

        public User AddUser(User user)
        {
            user.salt = Convert.ToBase64String(Common.GetRandomSalt(16));
            user.password = Convert.ToBase64String(Common.SaltHashPassword(
                    Encoding.ASCII.GetBytes(user.password),
                    Convert.FromBase64String(user.salt)));
            _userContext.Users.Add(user);
            _userContext.SaveChanges();
            return user;
        }

        public void DeleteUser(User user)
        {
            _userContext.Users.Remove(user);
            _userContext.SaveChanges(); 
        }

        public User EditUser(User user)
        {
            var existingUser = _userContext.Users.Find(user.id);
            if (existingUser != null)
            {
                existingUser.count_Quests = user.count_Quests;
                existingUser.count_Complete = user.count_Complete;
                existingUser.login = user.login;
                existingUser.name = user.name;
                existingUser.communication = user.communication;
                _userContext.Users.Update(existingUser);
                _userContext.SaveChanges();
            }
            return existingUser;
        }

        public User GetUser(int id)
        {
            var user = _userContext.Users.Find(id);
            return user;
        }

        public List<User> GetUsers()
        {
            return _userContext.Users.ToList();
        }
    }
}
