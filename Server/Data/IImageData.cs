using Server.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Server.Data
{
    public interface IImageData
    {
        List<Image> GetImages();

        Image GetImage(int id);

        Image AddImage(Image image);

        void DeleteImage(Image image);

        Image EditImage(Image image);
    }
}
