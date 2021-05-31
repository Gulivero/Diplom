using Server.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Server.Data
{
    public class SqlImageData : IImageData
    {
        private UserContext _userContext;
        public SqlImageData(UserContext userContext)
        {
            _userContext = userContext;
        }

        public Image AddImage(Image image)
        {
            _userContext.Images.Add(image);
            _userContext.SaveChanges();
            return image;
        }

        public void DeleteImage(Image image)
        {
            _userContext.Images.Remove(image);
            _userContext.SaveChanges();
        }

        public Image EditImage(Image image)
        {
            var existingImage = _userContext.Images.Find(image.id);
            if (existingImage != null)
            {
                existingImage.user_id = image.user_id;
                existingImage.quest_id = image.quest_id;
                existingImage.file_name = image.file_name;
                existingImage.image_ref = image.image_ref;
                _userContext.Images.Update(existingImage);
                _userContext.SaveChanges();
            }
            return existingImage;
        }

        public Image GetImage(int id)
        {
            var image = _userContext.Images.Find(id);
            return image;
        }

        public List<Image> GetImages()
        {
            return _userContext.Images.ToList();
        }
    }
}
