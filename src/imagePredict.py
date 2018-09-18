from keras.models import load_model
from keras.preprocessing.image import ImageDataGenerator

test_dir = 'pictest'
#test_dir = 'pictemp'
batch_size = 1
data_gen = ImageDataGenerator(rescale=1. / 255)
test_generator = data_gen.flow_from_directory(test_dir,
                                              color_mode='grayscale',
                                               target_size=(480, 640),
                                               batch_size=batch_size,
                                               class_mode=None#,
                                               #shuffle=False,
                                               #save_to_dir='E:/temp/1/',
                                               #save_prefix='test',
                                               #save_format='jpeg'
                                               )
# for i in range(55):
#     test_generator.next() 
model = load_model('myModel.h5')
predicted = model.predict_generator(generator=test_generator)
print(predicted)