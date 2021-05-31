using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Server.Models
{
    public class UserContext : DbContext
    {
        public UserContext(DbContextOptions<UserContext> options) : base(options)
        {

        }

        public DbSet<User> Users {get; set;}
        public DbSet<Quest> Quests { get; set; }
        public DbSet<Request> Requests { get; set; }
        public DbSet<Image> Images { get; set; }
    }
}
