using Server.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Server.Data
{
    public interface IQuestData
    {
        List<Quest> GetQuests();

        Quest GetQuest(int id);

        string AddQuest(Quest quest);

        void DeleteQuest(Quest quest);

        Quest EditQuest(Quest quest);
    }
}
