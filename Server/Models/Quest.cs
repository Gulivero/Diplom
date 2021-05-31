using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Threading.Tasks;

namespace Server.Models
{
    public class Quest
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int id { get; set; }
        [MaxLength(50)]
        public string name { get; set; }
        [MaxLength(500)]
        public string description { get; set; }
        [MaxLength(50)]
        public string reward { get; set; }
        [MaxLength(50)]
        public string author { get; set; }
        [MaxLength(250)]
        public string contractor { get; set; }
        public int author_id { get; set; }
        [MaxLength(50)]
        public string status { get; set; }
    }
}
