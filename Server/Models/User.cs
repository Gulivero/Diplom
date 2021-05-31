using System;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Server.Models
{
    public class User
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int id { get; set; }
        [MaxLength(50)]
        public string login { get; set; }
        [MaxLength(50)]
        public string password { get; set; }
        [MaxLength(50)]
        public string name { get; set; }
        [MaxLength(200)]
        public string communication { get; set; }
        [MaxLength(50)]
        public string role { get; set; }
        public int? count_Quests { get; set; }
        public int? count_Complete { get; set; }
        [MaxLength(250)]
        public string salt { get; set; }
    }
}
