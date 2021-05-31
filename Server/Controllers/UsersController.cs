using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Server.Models;
using Server.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace Server.Controllers
{   
    [ApiController]
    public class UsersController : ControllerBase
    {
        private IUserData _userData;

        public UsersController(IUserData userData)
        {
            _userData = userData;
        }

        [HttpGet]
        [Route("api/[controller]")]
        public string GetUsers()
        {
            //return Ok(_userData.GetUsers());
            return JsonConvert.SerializeObject(_userData.GetUsers());
        }

        [HttpGet]
        [Route("api/[controller]/{id}")]
        public string GetUser(int id)
        {
            var user = _userData.GetUser(id);

            if (user != null)
            {
                return JsonConvert.SerializeObject(user);
            }

            return JsonConvert.SerializeObject($"Пользователь с id: {id} не найден");
        }

        [HttpPost]
        [Route("api/[controller]")]
        public string AddUser(User user)
        {
            List<User> list = _userData.GetUsers();
            foreach (User user1 in list)
            {
                if (user1.login == user.login)
                    return JsonConvert.SerializeObject("Такой пользователь уже существует");
            }
            if (user.role == null)
                user.role = "user";
            if (user.count_Quests == null)
                user.count_Quests = 1;
            if (user.count_Complete == null)
                user.count_Complete = 0;
            _userData.AddUser(user);
            return JsonConvert.SerializeObject("Регистрация прошла успешно");

            //return Created(HttpContext.Request.Scheme + "://" + HttpContext.Request.Host + HttpContext.Request.Path + "/" + user.id, user);
        }

        [HttpDelete]
        [Route("api/[controller]/{id}")]
        public IActionResult DeleteUser(int id)
        {
            var user = _userData.GetUser(id);

            if (user != null)
            {
                _userData.DeleteUser(user);

                return Ok();
            }

            return NotFound($"Пользователь с id: {id} не найден");
        }

        [HttpPatch]
        [Route("api/[controller]/{id}")]
        public string EditUser(int id, User user)
        {
            var existingUser = _userData.GetUser(id);

            if (existingUser != null)
            {
                user.id = existingUser.id;
                if (user.count_Quests == null)
                    user.count_Quests = existingUser.count_Quests;
                if (user.count_Complete == null)
                    user.count_Complete = existingUser.count_Complete;
                if (user.login == null)
                    user.login = existingUser.login;
                if (user.password == null)
                    user.password = existingUser.password;
                if (user.name == null)
                    user.name = existingUser.name;
                if (user.communication == null)
                    user.communication = existingUser.communication;
                user = _userData.EditUser(user);
                return JsonConvert.SerializeObject("Изменение прошло успешно");
            }
            return JsonConvert.SerializeObject("Пользователь не найден");

        }
    }
}
