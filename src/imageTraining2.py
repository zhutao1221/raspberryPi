import keras
from keras.models import Model
from keras.layers import Conv2D, Dropout, Input, concatenate, Activation, AveragePooling2D, MaxPooling2D, Flatten, Dense
from keras.optimizers import SGD
from keras.preprocessing.image import ImageDataGenerator
from keras.layers.normalization import BatchNormalization
from keras import backend as K

if K.image_data_format() == 'channels_first':
    inputs = Input((1, 320, 240))
    channel_axis = 1
else:
    inputs = Input((240, 320, 1))
    channel_axis = -1 

def conv2d_bn(inX, nb_filter, num_row, num_col,
              padding='same', strides=(1, 1), use_bias=False):
    inX = Conv2D(nb_filter, (num_row, num_col),
                      strides=strides,
                      padding=padding,
                      use_bias=use_bias)(inX)
    inX = BatchNormalization(axis=channel_axis, scale=False)(inX)
    inX = Activation('relu')(inX)
    return inX

def inception(input):
#     branch_00 = conv2d_bn(input, 32, 3, 3, strides=(2,2), padding='valid')
#     branch_00 = conv2d_bn(branch_00, 32, 3, 3, padding='same')
#          
#     branch_01 = conv2d_bn(input, 32, 3, 3, strides=(2,2), padding='valid')
#     branch_02 = MaxPooling2D((3,3), strides=(2,2), padding='valid')(input)
#     
#     net = concatenate([branch_00, branch_01,branch_02], axis=channel_axis)
#     net = AveragePooling2D((4,4), padding='valid')(net)
#     
#     branch_10 = conv2d_bn(net, 32, 3, 3, strides=(2,2))
# #     branch_11 = conv2d_bn(net, 32, 5, 5, strides=(2,2))     
#     branch_11 = MaxPooling2D((3,3), strides=(2,2), padding='valid')(net)
#     
#     net = concatenate([branch_10, branch_11], axis=channel_axis)
#     net = AveragePooling2D((2,2), padding='valid')(net)

    net = conv2d_bn(input, 32, 3, 3, strides=(2,2), padding='valid')
    branch_0 = MaxPooling2D((3,3), strides=(2,2), padding='valid')(net)
    branch_1 = conv2d_bn(net, 32, 3, 3, strides=(2,2), padding='valid')
    net = concatenate([branch_0, branch_1], axis=channel_axis)

    branch_0 = conv2d_bn(net, 32, 3, 3, strides=(2,2), padding='valid')
    branch_1 = MaxPooling2D((3,3), strides=(2,2), padding='valid')(net)
    net = concatenate([branch_0, branch_1], axis=channel_axis)
    return net

train_dir = 'pic'
num_classes = 2
num_epochs = 120
batch_size = 50
data_gen = ImageDataGenerator(rescale=1. / 255)
train_generator = data_gen.flow_from_directory(train_dir,
                                               color_mode='grayscale',
                                               target_size=(240, 320),
                                               batch_size=batch_size,
                                               class_mode='categorical', 
                                               shuffle=True)

opt = SGD(nesterov=True)

x = inception(inputs)
x = AveragePooling2D((4,4), padding='valid')(x)
x = Dropout(0.1)(x)
x = Flatten()(x)
x = Dense(20,activation='relu')(x)
x = Dropout(0.1)(x)
x = Dense(units=num_classes, activation='softmax')(x)
model = Model(inputs, x, name='inception')
model.compile(loss=keras.losses.categorical_crossentropy, optimizer=opt)
model.summary()
 
model.fit_generator(generator=train_generator,
                    epochs=num_epochs)
 
model.save('myModel.h5')
