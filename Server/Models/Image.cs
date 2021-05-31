using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Threading.Tasks;

namespace Server.Models
{
    public class Image
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int id { get; set; }
        public int? user_id { get; set; }
        public int? quest_id { get; set; }
        [MaxLength(50)]
        public string file_name { get; set; }
        public string image_ref { get; set; }
    }
}
