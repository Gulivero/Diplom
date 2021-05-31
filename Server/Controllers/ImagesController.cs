using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using Server.Data;
using Server.Models;
using Server.Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Server.Controllers
{
    [ApiController]
    public class ImagesController : ControllerBase
    {
        private IImageData _imageData;

        public ImagesController(IImageData imageData)
        {
            _imageData = imageData;
        }

        [HttpGet]
        [Route("api/[controller]")]
        public string GetImages()
        {
            return JsonConvert.SerializeObject(_imageData.GetImages());
        }

        [HttpGet]
        [Route("api/[controller]/{id}")]
        public string GetImage(int id)
        {
            var image = _imageData.GetImage(id);

            if (image != null)
            {
                return JsonConvert.SerializeObject(image);
            }

            return JsonConvert.SerializeObject($"Фотография с id: {id} не найдена");
        }

        [HttpPost]
        [Route("api/[controller]")]
        public string AddImage(Image image)
        {
            _imageData.AddImage(image);
            return JsonConvert.SerializeObject("Добавление картинки прошло успешно");
        }

        [HttpDelete]
        [Route("api/[controller]/{id}")]
        public IActionResult DeleteImage(int id)
        {
            var image = _imageData.GetImage(id);

            if (image != null)
            {
                _imageData.DeleteImage(image);

                return Ok();
            }

            return NotFound($"Фотография с id: {id} не найдена");
        }

        [HttpPatch]
        [Route("api/[controller]/{id}")]
        public string EditImage(int id, Image image)
        {
            var existingImage = _imageData.GetImage(id);

            if (existingImage != null)
            {
                image.id = existingImage.id;
                if (image.user_id == null)
                    image.user_id = existingImage.user_id;
                if (image.quest_id == null)
                    image.quest_id = existingImage.quest_id;
                if (image.file_name == null)
                    image.file_name = existingImage.file_name;
                if (image.image_ref == null)
                    image.image_ref = existingImage.image_ref;
                image = _imageData.EditImage(image);
                return JsonConvert.SerializeObject("Изменение картинки прошло успешно");
            }
            return JsonConvert.SerializeObject("Фотография не найдена");

        }

    }
}
