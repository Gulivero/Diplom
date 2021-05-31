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
    public class RequestController : ControllerBase
    {
        private IRequestData _requestData;

        public RequestController(IRequestData requestData)
        {
            _requestData = requestData;
        }

        [HttpGet]
        [Route("api/[controller]")]
        public string GetRequests()
        {
            //return Ok(_questData.GetQuests());
            return JsonConvert.SerializeObject(_requestData.GetRequests());
        }

        [HttpGet]
        [Route("api/[controller]/{id}")]
        public string GetRequest(int id)
        {
            var request = _requestData.GetRequest(id);

            if (request != null)
            {
                return JsonConvert.SerializeObject(request);
            }

            return JsonConvert.SerializeObject($"Запрос с id: {id} не найден");
        }

        [HttpPost]
        [Route("api/[controller]")]
        public string AddRequest(Request request)
        {
            List<Request> list = _requestData.GetRequests();
            foreach (Request request1 in list)
            {
                if (request1.user_id == request.user_id && request1.quest_id == request.quest_id)
                    return JsonConvert.SerializeObject("Вы уже отправили запрос");
            }

            _requestData.AddRequest(request);

            return JsonConvert.SerializeObject("Запрос успешно отправлен");
            //return Created(HttpContext.Request.Scheme + "://" + HttpContext.Request.Host + HttpContext.Request.Path + "/" + quest.id,
            //    quest);
        }

        [HttpDelete]
        [Route("api/[controller]/{id}")]
        public string DeleteRequest(int id)
        {
            var request = _requestData.GetRequest(id);

            if (request != null)
            {
                _requestData.DeleteRequest(request);

                return JsonConvert.SerializeObject("Удаление прошло успешно");
            }

            return JsonConvert.SerializeObject($"Запрос с id: {id} не найден");
        }

        [HttpPatch]
        [Route("api/[controller]/{id}")]
        public string EditQuest(int id, Request request)
        {
            var existingRequest = _requestData.GetRequest(id);

            if (existingRequest != null)
            {
                request.id = existingRequest.id;
                if (request.user_id == null)
                    request.user_id = existingRequest.user_id;
                if (request.quest_id == null)
                    request.quest_id = existingRequest.quest_id;
                return JsonConvert.SerializeObject("Запрос успешно изменён");
            }

            return JsonConvert.SerializeObject($"Запрос с id: {id} не найден");
        }
    }
}
