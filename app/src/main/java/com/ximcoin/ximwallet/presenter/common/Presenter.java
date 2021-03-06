package com.ximcoin.ximwallet.presenter.common;

/*
 * Copyright 2018 Vandolf Estrellado
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Modifications copyright (C) 2018 Duchess Technologies

import android.os.Bundle;
import android.support.annotation.Nullable;

public interface Presenter {
    void start(@Nullable Bundle bundle);
    void stop();
    void resume();
    void pause();
    void saveState(@Nullable Bundle bundle);
}
