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
    public class QuestsController : ControllerBase
    {
        private IQuestData _questData;

        public QuestsController(IQuestData questData)
        {
            _questData = questData;
        }

        [HttpGet]
        [Route("api/[controller]")]
        public string GetQuests()
        {
            //return Ok(_questData.GetQuests());
            return JsonConvert.SerializeObject(_questData.GetQuests());
        }

        [HttpGet]
        [Route("api/[controller]/{id}")]
        public string GetQuest(int id)
        {
            var quest = _questData.GetQuest(id);

            if (quest != null)
            {
                return JsonConvert.SerializeObject(quest);
            }

            return JsonConvert.SerializeObject($"Квест с id: {id} не найден");
        }

        [HttpPost]
        [Route("api/[controller]")]
        public string AddQuest(Quest quest)
        {
            string answer = _questData.AddQuest(quest);

            return JsonConvert.SerializeObject(answer);
            //return Created(HttpContext.Request.Scheme + "://" + HttpContext.Request.Host + HttpContext.Request.Path + "/" + quest.id,
            //    quest);
        }

        [HttpDelete]
        [Route("api/[controller]/{id}")]
        public string DeleteQuest(int id)
        {
            var quest = _questData.GetQuest(id);

            if (quest != null)
            {
                _questData.DeleteQuest(quest);

                return JsonConvert.SerializeObject("Удаление прошло успешно");
            }

            return JsonConvert.SerializeObject($"Квест с id: {id} не найден");
        }

        [HttpPatch]
        [Route("api/[controller]/{id}")]
        public string EditQuest(int id, Quest quest)
        {
            var existingQuest = _questData.GetQuest(id);

            if (existingQuest != null)
            {
                quest.id = existingQuest.id;
                quest.author = existingQuest.author;
                quest.author_id = existingQuest.author_id;
                if (quest.name == null)
                    quest.name = existingQuest.name;
                if (quest.description == null)
                    quest.description = existingQuest.description;
                if (quest.reward == null)
                    quest.reward = existingQuest.reward;
                if (quest.contractor == null || quest.contractor == "Не выбран")
                    quest.contractor = existingQuest.contractor;
                if (quest.status == null || quest.status == "none")
                    quest.status = existingQuest.status;
                quest = _questData.EditQuest(quest);
                return JsonConvert.SerializeObject("Квест успешно изменён");
            }

            return JsonConvert.SerializeObject($"Квест с id: {id} не найден");
        }
    }
}
