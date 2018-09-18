from keras.preprocessing.image import ImageDataGenerator
from keras.models import load_model

train_dir = 'pic'
batch_size = 50
num_epochs = 30
data_gen = ImageDataGenerator(rescale=1. / 255)
train_generator = data_gen.flow_from_directory(train_dir,
                                               color_mode='grayscale',
                                               target_size=(240, 320),
                                               batch_size=batch_size,
                                               class_mode='categorical', 
                                               shuffle=True)


model = load_model('myModel.h5')

model.summary()
 
model.fit_generator(generator=train_generator,
                    epochs=num_epochs)
 
model.save('myModel2nd.h5')