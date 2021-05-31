using Server.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Server.Data
{
    public class SqlQuestData : IQuestData
    {
        private UserContext _userContext;
        public SqlQuestData(UserContext userContext)
        {
            _userContext = userContext;
        }

        public string AddQuest(Quest quest)
        {
            User user = _userContext.Users.Where(u => u.login.Equals(quest.author)).First();
            if (user.count_Quests > 0)
            {
                _userContext.Quests.Add(quest);
                user.count_Quests -= 1;
                _userContext.SaveChanges();
                return "Квест успешно добавлен";
            }
            else
                return "Вы достигли лимита квестов";
        }

        public void DeleteQuest(Quest quest)
        {
            List<Request> list = _userContext.Requests.ToList();
            foreach (Request request in list)
            {
                if (request.quest_id == quest.id)
                {
                    _userContext.Requests.Remove(request);
                    _userContext.SaveChanges();
                }
            }
            List<Image> list2 = _userContext.Images.ToList();
            foreach (Image image in list2)
            {
                if (image.quest_id == quest.id)
                {
                    _userContext.Images.Remove(image);
                    _userContext.SaveChanges();
                }
            }
            _userContext.Quests.Remove(quest);
            _userContext.SaveChanges();
        }

        public Quest EditQuest(Quest quest)
        {
            var existingQuest = _userContext.Quests.Find(quest.id);
            if (existingQuest != null)
            {
                existingQuest.name = quest.name;
                existingQuest.description = quest.description;
                existingQuest.reward = quest.reward;
                existingQuest.contractor = quest.contractor;
                existingQuest.status = quest.status;
                _userContext.Quests.Update(existingQuest);
                _userContext.SaveChanges();
            }
            return existingQuest;
        }

        public Quest GetQuest(int id)
        {
            var quest = _userContext.Quests.Find(id);
            return quest;
        }

        public List<Quest> GetQuests()
        {
            return _userContext.Quests.ToList();

        }
    }
}
