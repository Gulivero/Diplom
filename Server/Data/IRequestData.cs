using Server.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Server.Data
{
    public interface IRequestData
    {
        List<Request> GetRequests();

        Request GetRequest(int id);

        Request AddRequest(Request request);

        void DeleteRequest(Request request);

        Request EditRequest(Request request);
    }
}
