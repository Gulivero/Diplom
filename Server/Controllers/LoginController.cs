using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using Server.Data;
using Server.Models;
using Server.Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Server.Controllers
{
    [ApiController]
    public class LoginController : ControllerBase
    {
        private IUserData _userData;

        public LoginController(IUserData userData)
        {
            _userData = userData;
        }

        [HttpPost]
        [Route("api/[controller]")]
        public string Login(User user)
        {
            List<User> list = _userData.GetUsers();
            foreach (User user1 in list)
            {
                //Calculate hash password from data of Client and compare with hash in server with salt
                var client_post_hash_password = Convert.ToBase64String(
                    Common.SaltHashPassword(
                        Encoding.ASCII.GetBytes(user.password),
                        Convert.FromBase64String(user1.salt)));
                if (user.login == user1.login && client_post_hash_password.Equals(user1.password))
                    return JsonConvert.SerializeObject(user1);
            }
            return JsonConvert.SerializeObject("Пользователь не найден");
        }
    }
}
