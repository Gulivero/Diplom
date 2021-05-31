using Server.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Server.Data
{
    public class SqlRequestData : IRequestData
    {
        private UserContext _userContext;
        public SqlRequestData(UserContext userContext)
        {
            _userContext = userContext;
        }

        public Request AddRequest(Request request)
        {
            _userContext.Requests.Add(request);
            _userContext.SaveChanges();
            return request;
        }

        public void DeleteRequest(Request request)
        {
            _userContext.Requests.Remove(request);
            _userContext.SaveChanges();
        }

        public Request EditRequest(Request request)
        {
            var existingRequest = _userContext.Requests.Find(request.id);
            if (existingRequest != null)
            {
                existingRequest.user_id = request.user_id;
                existingRequest.quest_id = request.quest_id;
                _userContext.Requests.Update(existingRequest);
                _userContext.SaveChanges();
            }
            return existingRequest;
        }

        public Request GetRequest(int id)
        {
            var request = _userContext.Requests.Find(id);
            return request;
        }

        public List<Request> GetRequests()
        {
            return _userContext.Requests.ToList();

        }
    }
}
